package com.example.traintravel.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Firebase {
    val usersCollectionRef = FirebaseFirestore.getInstance().collection("users")
    val usersListLiveData : MutableLiveData<List<Users>> by lazy {
        MutableLiveData<List<Users>>()
    }

    private val ticketsCollectionRef = FirebaseFirestore.getInstance().collection("tickets")
    val ticketsListLiveData : MutableLiveData<List<Ticket>> by lazy {
        MutableLiveData<List<Ticket>>()
    }

    private val purchasedTicketsCollectionRef = FirebaseFirestore.getInstance().collection("purchasedTickets")
    val purchasedTicketsListLiveData : MutableLiveData<List<PurchasedTicket>> by lazy {
        MutableLiveData<List<PurchasedTicket>>()
    }

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

    fun addTicket(ticket: Ticket) {
        ticketsCollectionRef.add(ticket).addOnSuccessListener { documentReference ->
            ticket.id = documentReference.id
            documentReference.set(ticket).addOnFailureListener {
                Log.d("Firebase", "Error adding ticket id: ", it)
            }
        }
    }

    fun updateTicket(ticket: Ticket) {
        ticketsCollectionRef.document(ticket.id).set(ticket).addOnFailureListener {
            Log.d("Firebase", "Error updating ticket", it)
        }
    }

    fun deleteTicket(ticket: Ticket) {
        if (ticket.id.isEmpty()) {
            Log.d("Firebase", "Error delete item: ticket Id is empty!")
            return
        }
        // Menghapus dokumen ticket dari Firestore berdasarkan ID
        ticketsCollectionRef.document(ticket.id).delete().addOnFailureListener {
            Log.d("Firebase", "Error deleting ticket", it)
        }
    }

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

    fun deletePurchasedTicket(purchasedTicket: PurchasedTicket) {
        if (purchasedTicket.id.isEmpty()) {
            Log.d("Firebase", "Error delete item: ticket Id is empty!")
            return
        }
        // Menghapus dokumen ticket dari Firestore berdasarkan ID
        purchasedTicketsCollectionRef.document(purchasedTicket.id).delete().addOnFailureListener {
            Log.d("Firebase", "Error deleting purchased ticket", it)
        }
    }

    fun getUser(userId : String) : Users? {
        var user: Users ?= null
        usersCollectionRef.document(userId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                user = document.toObject(Users::class.java)
            }
        }.addOnFailureListener {
            Log.d("FirebaseAuth", "User data not found: ", it)
        }
        return user
    }

    fun getTicket(ticketId : String) : Ticket? {
        for (ticket in ticketsListLiveData.value!!) {
            if (ticketId == ticket.id) {
                return ticket
            }
        }
        return null
    }

    fun getPurchasedTicket(ticketId : String, userId : String) : PurchasedTicket? {
        for (purchasedTicket in purchasedTicketsListLiveData.value!!) {
            if (purchasedTicket.ticketId == ticketId && purchasedTicket.userId == userId) {
                return purchasedTicket
            }
        }
        return null
    }

    fun convertStringToDate(dateString: String): Date {
        val format = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }

    fun getUpcomingTripTicket(userId : String) : Pair<Ticket?, PurchasedTicket?> {
        val purchasedTickets = purchasedTicketsListLiveData.value.orEmpty()
        val userPurchasedTickets = purchasedTickets.filter { it.userId == userId }

        val userTickets = ticketsListLiveData.value.orEmpty().filter { ticket ->
            userPurchasedTickets.any { it.ticketId == ticket.id }
        }

        val currentDate = Calendar.getInstance().time

        val upcomingTrips = userTickets.filter {
            val departureDate = convertStringToDate(it.departureDate)
            departureDate >= currentDate
        }

        val ticket = upcomingTrips.sortedBy { convertStringToDate(it.departureDate) }.firstOrNull()
        val purchasedTicket = userPurchasedTickets.filter { it.ticketId == ticket?.id }.firstOrNull()

        return Pair(ticket, purchasedTicket)
    }
}