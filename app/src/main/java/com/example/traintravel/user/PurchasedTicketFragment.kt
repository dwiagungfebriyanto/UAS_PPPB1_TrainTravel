package com.example.traintravel.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.data.Firebase
import com.example.traintravel.databinding.FragmentPurchasedTicketBinding
import com.example.traintravel.ticket.PurchasedTicketAdapter

// Fragment untuk menampilkan tiket yang telah dibeli oleh pengguna
class PurchasedTicketFragment : Fragment() {
    private var _binding: FragmentPurchasedTicketBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPurchasedTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(requireContext())
        observeTickets()
    }

    // Mengamati perubahan pada daftar tiket yang telah dibeli
    private fun observeTickets() {
        Firebase.purchasedTicketsListLiveData.observe(viewLifecycleOwner) { purchasedTickets ->
            // Filter tiket berdasarkan ID pengguna
            val userPurchasedTicket = purchasedTickets.filter { it.userId == prefManager.getUserId() }.sortedByDescending { Firebase.convertStringToDate(it.purchaseDate) }

            // Membuka Fragment Detail Tiket
            val adapterTicket = PurchasedTicketAdapter(userPurchasedTicket) { purchasedTicket ->
                PurchasedTicketDetailFragment(purchasedTicket).show(parentFragmentManager, "Purchased Ticket Detail")
            }

            with(binding) {
                progressBar.visibility = View.GONE
                txtEmpty.visibility = if (userPurchasedTicket.isEmpty()) View.VISIBLE else View.GONE
                rvTicket.apply {
                    adapter = adapterTicket
                    layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
    }
}