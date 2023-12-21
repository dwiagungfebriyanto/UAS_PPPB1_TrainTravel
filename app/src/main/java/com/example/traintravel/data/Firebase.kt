package com.example.traintravel.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Firebase {
    // Referensi ke koleksi "users" di Firestore
    val usersCollectionRef = FirebaseFirestore.getInstance().collection("users")
    val usersListLiveData : MutableLiveData<List<Users>> by lazy {
        MutableLiveData<List<Users>>()
    }

    // Referensi ke koleksi "tickets" di Firestore
    private val ticketsCollectionRef = FirebaseFirestore.getInstance().collection("tickets")
    val ticketsListLiveData : MutableLiveData<List<Ticket>> by lazy {
        MutableLiveData<List<Ticket>>()
    }

    // Referensi ke koleksi "purchasedTickets" di Firestore
    private val purchasedTicketsCollectionRef = FirebaseFirestore.getInstance().collection("purchasedTickets")
    val purchasedTicketsListLiveData : MutableLiveData<List<PurchasedTicket>> by lazy {
        MutableLiveData<List<PurchasedTicket>>()
    }

    // Mendengarkan perubahan data pada koleksi "users", "tickets", dan "purchasedTickets" di Firestore
    fun observeDataChanges() {
        usersCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("Firebase", "Error listening for users changes", error)
            }
            val users = snapshots?.toObjects(Users::class.java)
            if (users != null) {
                usersListLiveData.postValue(users)
            }
        }
        ticketsCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("Firebase", "Error listening for tickets changes", error)
            }
            val tickets = snapshots?.toObjects(Ticket::class.java)
            if (tickets != null) {
                ticketsListLiveData.postValue(tickets)
            }
        }
        purchasedTicketsCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("Firebase", "Error listening for purchased tickets changes", error)
            }
            val purchasedTickets = snapshots?.toObjects(PurchasedTicket::class.java)
            if (purchasedTickets != null) {
                purchasedTicketsListLiveData.postValue(purchasedTickets)
            }
        }
    }

    // Menambahkan listener perubahan data pada koleksi "purchasedTickets" di Firestore
    fun observePurchasedTicket() {
        purchasedTicketsCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("Firebase", "Error listening for purchased tickets changes", error)
            }
            val purchasedTickets = snapshots?.toObjects(PurchasedTicket::class.java)
            if (purchasedTickets != null) {
                purchasedTicketsListLiveData.postValue(purchasedTickets)
            }
        }
    }

    // Menambahkan data tiket ke koleksi "tickets" di Firestore
    fun addTicket(ticket: Ticket) {
        ticketsCollectionRef.add(ticket).addOnSuccessListener { documentReference ->
            ticket.id = documentReference.id
            documentReference.set(ticket).addOnFailureListener {
                Log.d("Firebase", "Error adding ticket id: ", it)
            }
        }
    }

    // Memperbarui data tiket pada koleksi "tickets" di Firestore
    fun updateTicket(ticket: Ticket) {
        ticketsCollectionRef.document(ticket.id).set(ticket).addOnFailureListener {
            Log.d("Firebase", "Error updating ticket", it)
        }
    }

    // Menghapus data tiket dari koleksi "tickets" di Firestore
    fun deleteTicket(ticket: Ticket) {
        if (ticket.id.isEmpty()) {
            Log.d("Firebase", "Error delete item: ticket Id is empty!")
            return
        }
        ticketsCollectionRef.document(ticket.id).delete().addOnFailureListener {
            Log.d("Firebase", "Error deleting ticket", it)
        }
    }

    // Menambahkan data tiket yang dibeli ke koleksi "purchasedTickets" di Firestore
    fun addPurchasedTicket(purchasedTicket: PurchasedTicket) : Boolean {
        var isAdded = false
        for (ticket in purchasedTicketsListLiveData.value!!) {
            if (purchasedTicket.ticketId.equals(ticket.ticketId) && purchasedTicket.userId.equals(ticket.userId)) {
                isAdded = true
            }
        }
        if (!isAdded) {
            purchasedTicketsCollectionRef.add(purchasedTicket).addOnSuccessListener { documentReference ->
                purchasedTicket.id = documentReference.id
                documentReference.set(purchasedTicket).addOnFailureListener {
                    Log.d("Firebase", "Error adding purchased ticket id: ", it)
                }
            }
        }
        return isAdded
    }

    // Menghapus data tiket yang dibeli dari koleksi "purchasedTickets" di Firestore
    fun deletePurchasedTicket(purchasedTicket: PurchasedTicket) {
        if (purchasedTicket.id.isEmpty()) {
            Log.d("Firebase", "Error delete item: ticket Id is empty!")
            return
        }
        purchasedTicketsCollectionRef.document(purchasedTicket.id).delete().addOnFailureListener {
            Log.d("Firebase", "Error deleting purchased ticket", it)
        }
    }

    // Mendapatkan objek Tiket berdasarkan ID
    fun getTicket(ticketId : String) : Ticket? {
        for (ticket in ticketsListLiveData.value!!) {
            if (ticketId == ticket.id) {
                return ticket
            }
        }
        return null
    }

    // Mendapatkan objek Tiket yang dibeli berdasarkan ID Tiket dan ID Pengguna
    fun getPurchasedTicket(ticketId : String, userId : String) : PurchasedTicket? {
        for (purchasedTicket in purchasedTicketsListLiveData.value!!) {
            if (purchasedTicket.ticketId == ticketId && purchasedTicket.userId == userId) {
                return purchasedTicket
            }
        }
        return null
    }

    // Mengonversi string tanggal menjadi objek Date
    fun convertStringToDate(dateString: String): Date {
        val format = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }

    // Menentukan apakah tanggal tertentu adalah hari ini
    fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply { time = date }
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
    }

    // Mendapatkan tiket perjalanan yang akan datang berdasarkan ID Pengguna
    fun getUpcomingTripTicket(userId : String) : Pair<Ticket?, PurchasedTicket?> {
        val purchasedTickets = purchasedTicketsListLiveData.value.orEmpty()
        val userPurchasedTickets = purchasedTickets.filter { it.userId == userId }

        val userTickets = ticketsListLiveData.value.orEmpty().filter { ticket ->
            userPurchasedTickets.any { it.ticketId == ticket.id }
        }

        val currentDate = Calendar.getInstance().time

        val upcomingTrips = userTickets.filter {
            convertStringToDate(it.departureDate) >= currentDate || isToday(convertStringToDate(it.departureDate))
        }

        val ticket = upcomingTrips.minByOrNull { convertStringToDate(it.departureDate) }
        val purchasedTicket = userPurchasedTickets.firstOrNull { it.ticketId == ticket?.id }

        return Pair(ticket, purchasedTicket)
    }
}