package com.example.traintravel.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.data.Firebase
import com.example.traintravel.databinding.FragmentHomeBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        val (upcomingTrip, purchasedTicket) = Firebase.getUpcomingTripTicket(prefManager.getUserId())

        with(binding) {
            txtHome.text = "Hi, ${prefManager.getUsername()}!"

            if (upcomingTrip != null) {
                txtTrainName.text       = upcomingTrip.trainName
                txtPrice.text           = NumberFormat.getNumberInstance(Locale("id")).format(purchasedTicket?.totalPrice)
                txtDeparture.text       = upcomingTrip.departureStation
                txtDepartureTime.text   = upcomingTrip.departureTime
                txtDestination.text     = upcomingTrip.destinationStation
                txtArrivalTime.text     = upcomingTrip.arrivalTime
                txtClass.text           = upcomingTrip.classType
                txtDepartureDate.text   = getDate(upcomingTrip.departureDate)

                if (purchasedTicket != null) {
                    btnToDetailTicket.setOnClickListener {
                        PurchasedTicketDetailFragment(purchasedTicket).show(parentFragmentManager, "Purchased Ticket Detail")
                    }
                }

            } else {
                Toast.makeText(context, "RA KETEMU BLOK", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDate(departuredDate : String) : String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val date: Date = inputFormat.parse(departuredDate) ?: Date()
        return outputFormat.format(date)
    }
}