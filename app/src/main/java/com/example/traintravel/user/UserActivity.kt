package com.example.traintravel.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.R
import com.example.traintravel.data.Firebase
import com.example.traintravel.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this)

        with(binding) {
            // Menghubungkan BottomNavigationView dengan NavController
            val navController = findNavController(R.id.nav_host_fragment)
            bottomNavigationView.setupWithNavController(navController)
        }

        // Memeriksa apakah intent memiliki extra "ticketId"
        if (intent.hasExtra("ticketId")) {
            // Mendapatkan ID tiket dari intent
            val ticketId = intent.getStringExtra("ticketId").toString()

            // Mendapatkan objek PurchasedTicket dari Firebase berdasarkan ID tiket dan ID pengguna
            val purchasedTicket = Firebase.getPurchasedTicket(ticketId, prefManager.getUserId())

            // Menampilkan detail tiket yang dibeli menggunakan fragment
            PurchasedTicketDetailFragment(purchasedTicket!!).show(supportFragmentManager, "Purchased Ticket Detail")
        }
    }
}
