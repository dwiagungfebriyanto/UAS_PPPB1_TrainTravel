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
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Fragment untuk menampilkan profil pengguna dan mengelola aksi logout
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private lateinit var firebaseAuth: FirebaseAuth
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
        firebaseAuth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // Menampilkan informasi pengguna seperti nama, email, dan tanggal lahir
            txtUsername.text = prefManager.getUsername()
            txtEmail.text = prefManager.getUserEmail()
            txtBirthdate.text = prefManager.getUserBirthdate()
            txtFirstLetter.text = prefManager.getUsername().first().toString().uppercase()

            // Menangani logout ketika tombol logout ditekan
            btnLogout.setOnClickListener {
                logout()
            }
        }

        // Mengamati jumlah tiket yang telah dibeli dan jumlah tiket favorit untuk ditampilkan di profil
        observeTickets()
    }

     // Mengamati jumlah tiket yang telah dibeli dan jumlah tiket favorit untuk ditampilkan di profil
    private fun observeTickets() {
        Firebase.purchasedTicketsListLiveData.observe(viewLifecycleOwner) { purchasedTickets ->
            // Memfilter tiket yang telah dibeli oleh pengguna
            val listPurchasedTickets = purchasedTickets.filter { it.userId == prefManager.getUserId() }
            binding.txtPurchasedTicketCount.text = listPurchasedTickets.size.toString()
        }

        executorService = Executors.newSingleThreadExecutor()
        val db = FavoriteTicketRoomDatabase.getDatabase(requireContext())
        mFavoriteTicketDao = db!!.favoriteTicketDuo()!!

        // Mengamati jumlah tiket favorit yang dimiliki pengguna
        mFavoriteTicketDao.getFavoriteTicketLiveData(prefManager.getUserId()).observe(viewLifecycleOwner) { favoriteTickets ->
            binding.txtFavoriteTicketCount.text = favoriteTickets.size.toString()
        }
    }

    // Menangani proses logout pengguna
    private fun logout() {
        // Membuat dialog konfirmasi untuk logout
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
            // Melakukan proses logout menggunakan FirebaseAuth
            firebaseAuth.signOut()

            // Menghapus data pengguna dari PrefManager
            prefManager.clear()

            // Menavigasi pengguna ke halaman login (AuthActivity)
            startActivity(Intent(context, AuthActivity::class.java))

            // Menutup aktivitas saat ini
            requireActivity().finish()
            dialog.dismiss()
        }

        // Menampilkan dialog konfirmasi logout
        dialog.show()
    }
}