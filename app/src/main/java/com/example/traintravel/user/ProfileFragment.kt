package com.example.traintravel.user

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.R
import com.example.traintravel.auth.AuthActivity
import com.example.traintravel.data.FavoriteTicketDao
import com.example.traintravel.data.FavoriteTicketRoomDatabase
import com.example.traintravel.data.Firebase
import com.example.traintravel.databinding.FragmentProfileBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        prefManager = PrefManager.getInstance(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = Firebase.getUser(prefManager.getUsername())

        with(binding) {
            txtUsername.text = user?.username
            txtEmail.text = user?.email
            txtBirthdate.text = user?.birthDate
            txtFirstLetter.text = user?.username?.first().toString().uppercase()

            btnLogout.setOnClickListener {
                logout()
            }
        }

        observeTickets()
    }

    private fun observeTickets() {
        Firebase.purchasedTicketsListLiveData.observe(viewLifecycleOwner) { purchasedTickets ->
            val listPurchasedTickets = purchasedTickets.filter { it.userId == prefManager.getUserId() }
            binding.txtPurchasedTicketCount.text = listPurchasedTickets.size.toString()
        }

        executorService = Executors.newSingleThreadExecutor()
        val db = FavoriteTicketRoomDatabase.getDatabase(requireContext())
        mFavoriteTicketDao = db!!.favoriteTicketDuo()!!

        mFavoriteTicketDao.getFavoriteTicketLiveData(prefManager.getUserId()).observe(viewLifecycleOwner) { favoriteTickets ->
            binding.txtFavoriteTicketCount.text = favoriteTickets.size.toString()
        }
    }

    private fun logout() {
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

        confirmationTxt.text = "Are you sure you want to logout?"

        // Menangani klik tombol Cancel pada dialog
        cancelTxt.setOnClickListener {
            dialog.dismiss()
        }

        // Menangani klik tombol Yes pada dialog
        yesTxt.setOnClickListener {
            prefManager.setLoggedIn(false)
            startActivity(Intent(context, AuthActivity::class.java))
            requireActivity().finish()
            dialog.dismiss()
        }

        // Menampilkan dialog
        dialog.show()
    }
}