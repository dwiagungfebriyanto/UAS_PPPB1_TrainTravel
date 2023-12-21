package com.example.traintravel.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.PurchasedTicket
import com.example.traintravel.data.Ticket
import com.example.traintravel.databinding.FragmentHomeBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Fragment untuk menampilkan halaman utama pengguna, termasuk informasi perjalanan mendatang dan riwayat pembelian tiket
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(requireContext())

        // Mendapatkan informasi perjalanan mendatang dan tiket yang telah dibeli
        val (upcomingTrip, purchasedTicket) = Firebase.getUpcomingTripTicket(prefManager.getUserId())

        with(binding) {
            // Menampilkan pesan selamat datang kepada pengguna
            txtHome.text = "Hi, ${prefManager.getUsername()}!"

            if (upcomingTrip != null) {
                // Menampilkan detail perjalanan mendatang jika ada
                txtTrainName.text       = upcomingTrip.trainName
                txtPrice.text           = NumberFormat.getNumberInstance(Locale("id")).format(purchasedTicket?.totalPrice)
                txtDeparture.text       = upcomingTrip.departureStation
                txtDepartureTime.text   = upcomingTrip.departureTime
                txtDestination.text     = upcomingTrip.destinationStation
                txtArrivalTime.text     = upcomingTrip.arrivalTime
                txtClass.text           = upcomingTrip.classType
                txtDepartureDate.text   = getDate(upcomingTrip.departureDate)

                if (purchasedTicket != null) {
                    // Menambahkan aksi klik pada tombol untuk melihat detail tiket yang telah dibeli
                    btnToDetailTicket.setOnClickListener {
                        PurchasedTicketDetailFragment(purchasedTicket).show(parentFragmentManager, "Purchased Ticket Detail")
                    }
                }

            } else {
                // Menyembunyikan beberapa elemen jika tidak ada upcoming trip
                txtDepartureDate.visibility = View.GONE
                btnToDetailTicket.visibility = View.GONE
                classDuration.visibility = View.GONE
                txtEmpty.visibility = View.VISIBLE
            }
        }

        // Menambahkan listener untuk menangani tampilan kalendar dan menampilkan detail tiket jika ditemukan pada tanggal yang dipilih
        calendarViewHistory()
    }

    // Mengonversi format tanggal dari "dd/MM/yyyy" ke "d MMMM yyyy"
    private fun getDate(departuredDate: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val date: Date = inputFormat.parse(departuredDate) ?: Date()
        return outputFormat.format(date)
    }


    // Menangani tampilan kalendar untuk menampilkan riwayat pembelian tiket pada tanggal tertentu
    private fun calendarViewHistory() {
        Firebase.purchasedTicketsListLiveData.observe(viewLifecycleOwner) { purchasedTickets ->
            // Memfilter tiket yang telah dibeli oleh pengguna
            val userPurchasedTickets = purchasedTickets.filter { it.userId == prefManager.getUserId() }
            val tickets: MutableList<Pair<PurchasedTicket, Ticket>> = mutableListOf()
            for (purchasedTicket in userPurchasedTickets) {
                // Mendapatkan informasi tiket yang sesuai dengan tiket yang telah dibeli
                val ticket = Firebase.getTicket(purchasedTicket.ticketId)
                if (ticket != null) {
                    val pair = Pair(purchasedTicket, ticket)
                    tickets.add(pair)
                }
            }

            // Menangani aksi ketika pengguna memilih tanggal pada kalendar
            binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                var hasTrip = false
                for (ticket in tickets) {
                    if (selectedDate == ticket.second.departureDate) {
                        hasTrip = true
                        Toast.makeText(context, "You found a trip! Wait...", Toast.LENGTH_SHORT).show()
                        PurchasedTicketDetailFragment(ticket.first).show(parentFragmentManager, "Purchased Ticket Detail")
                        break
                    }
                }
                if (!hasTrip) {
                    Toast.makeText(context, "No trip", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}