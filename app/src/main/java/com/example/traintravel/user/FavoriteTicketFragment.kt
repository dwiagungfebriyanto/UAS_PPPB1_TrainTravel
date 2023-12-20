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
import com.example.traintravel.data.Firebase
import com.example.traintravel.databinding.FragmentFavoriteTicketBinding
import com.example.traintravel.ticket.FavoriteTicketAdapter
import com.example.traintravel.ticket.TicketDetailFragment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteTicketFragment : Fragment() {
    private var _binding: FragmentFavoriteTicketBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private lateinit var mFavoriteTicketDao: FavoriteTicketDao
    private lateinit var executorService: ExecutorService
    private lateinit var confirmationTxt: TextView
    private lateinit var cancelTxt: TextView
    private lateinit var yesTxt: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteTicketBinding.inflate(inflater, container, false)
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

    private fun getAllFavoriteTickets() {
        mFavoriteTicketDao.getFavoriteTicketLiveData(prefManager.getUserId()).observe(viewLifecycleOwner) { favoriteTickets ->
            val adapterFavoriteTicket = FavoriteTicketAdapter(
                favoriteTickets,
                { favoriteTicket ->
                    TicketDetailFragment(null, favoriteTicket).show(parentFragmentManager, "Ticket Detail")
                },
                { favoriteTicket ->
                    deleteFavoriteTicket(favoriteTicket)
                },
                { ticketId ->
                    AddAdditionalPackagesFragment(Firebase.getTicket(ticketId)!!).show(parentFragmentManager, "Add Extra Packages")
                })

            with(binding) {
                progressBar.visibility = View.GONE
                txtEmpty.visibility = if (favoriteTickets.isEmpty()) View.VISIBLE else View.GONE
                rvTicket.apply {
                    adapter = adapterFavoriteTicket
                    layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
    }

    private fun deleteFavoriteTicket(favoriteTicket: FavoriteTicket) {
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

        confirmationTxt.text = "Remove this ticket from your favorites list?"

        // Menangani klik tombol Cancel pada dialog
        cancelTxt.setOnClickListener {
            dialog.dismiss()
        }

        // Menangani klik tombol Yes pada dialog
        yesTxt.setOnClickListener {
            executorService.execute { mFavoriteTicketDao.deleteFavoriteTicket(favoriteTicket) }
            Toast.makeText(context, "The ticket was successfully removed from the favorites list.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        // Menampilkan dialog
        dialog.show()
    }
}