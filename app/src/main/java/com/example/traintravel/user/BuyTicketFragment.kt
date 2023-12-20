package com.example.traintravel.user

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.R
import com.example.traintravel.data.FavoriteTicket
import com.example.traintravel.data.FavoriteTicketDao
import com.example.traintravel.data.FavoriteTicketRoomDatabase
import com.example.traintravel.ticket.TicketAdapter
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.Ticket
import com.example.traintravel.databinding.FragmentBuyTicketBinding
import com.example.traintravel.ticket.TicketDetailFragment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BuyTicketFragment : Fragment() {
    private var _binding: FragmentBuyTicketBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private lateinit var mFavoriteTicketDao: FavoriteTicketDao
    private lateinit var executorService: ExecutorService
    private var listFavoriteTickets = emptyList<FavoriteTicket>()
    private lateinit var confirmationTxt: TextView
    private lateinit var cancelTxt: TextView
    private lateinit var yesTxt: TextView
    private var adapterTicket: TicketAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBuyTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(requireContext())

        executorService = Executors.newSingleThreadExecutor()
        val db = FavoriteTicketRoomDatabase.getDatabase(requireContext())
        mFavoriteTicketDao = db!!.favoriteTicketDuo()!!

        getAllFavoriteTickets()
    }

    private fun observeTickets() {
        Firebase.ticketsListLiveData.observe(viewLifecycleOwner) { tickets ->
            adapterTicket = TicketAdapter(
                tickets,
                { ticket ->
                    TicketDetailFragment(ticket).show(parentFragmentManager, "Ticket Detail")
                },
                { ticket ->
                    AddAdditionalPackagesFragment(ticket).show(parentFragmentManager, "Add Extra Packages")
                },
                { ticket ->
                    if (!isOnTheFavoriteList(ticket)) {
                        insertFavoriteTicket(ticket)
                    } else {
                        Toast.makeText(context, "This ticket is already on your favorites list.", Toast.LENGTH_SHORT).show()
                    }
                },
                listFavoriteTickets
            )

            binding.progressBar.visibility = View.GONE

            binding.rvTicket.apply {
                adapter = adapterTicket
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    fun getAllFavoriteTickets() {
        mFavoriteTicketDao.getFavoriteTicketLiveData(prefManager.getUserId()).observe(viewLifecycleOwner) { newListFavoriteTickets ->
            listFavoriteTickets = newListFavoriteTickets
            adapterTicket?.updateFavoriteList(listFavoriteTickets)
            observeTickets()
        }
    }

    private fun isOnTheFavoriteList(ticket: Ticket) : Boolean {
        for (favoriteTicket in listFavoriteTickets) {
            if (ticket.id == favoriteTicket.ticketId) {
                return true
            }
        }
        return false
    }

    private fun insertFavoriteTicket(ticket: Ticket) {
        val favoriteTicket = FavoriteTicket(
            ticketId = ticket.id,
            userId = prefManager.getUserId(),
            trainName = ticket.trainName,
            departureDate = ticket.departureDate,
            price = ticket.price,
            classType = ticket.classType,
            departureStation = ticket.departureStation,
            departureTime = ticket.departureTime,
            destinationStation = ticket.destinationStation,
            arrivalTime = ticket.arrivalTime,
            tripDuration = ticket.tripDuration
        )

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_confirm)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.window!!.attributes.gravity = Gravity.BOTTOM
        dialog.window!!.attributes.windowAnimations = R.style.animation
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        confirmationTxt = dialog.findViewById(R.id.txt_confirmation)
        cancelTxt = dialog.findViewById(R.id.txt_cancel)
        yesTxt = dialog.findViewById(R.id.txt_yes)

        confirmationTxt.text = "Add this ticket to your favorite list?"

        // Menangani klik tombol Cancel pada dialog
        cancelTxt.setOnClickListener {
            dialog.dismiss()
        }

        // Menangani klik tombol Yes pada dialog
        yesTxt.setOnClickListener {
            executorService.execute{ mFavoriteTicketDao.insertFavoriteTicket(favoriteTicket) }
            Toast.makeText(context, "Successfully added the ticket to your favorites list.", Toast.LENGTH_SHORT).show()
            getAllFavoriteTickets()
            dialog.dismiss()
        }

        // Menampilkan dialog
        dialog.show()
    }
}