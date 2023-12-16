package com.example.traintravel.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
            val navController = findNavController(R.id.nav_host_fragment)
            bottomNavigationView.setupWithNavController(navController)
        }

        Log.d("UserActivity", intent.action.toString())

        if (intent.hasExtra("ticketId")) {
            Log.d("UserActivity", intent.getStringExtra("ticketId").toString())
            val ticketId = intent.getStringExtra("ticketId").toString()
            val purchasedTicket = Firebase.getPurchasedTicket(ticketId, prefManager.getUserId())
            PurchasedTicketDetailFragment(purchasedTicket!!).show(supportFragmentManager, "Purchased Ticket Detail")
        }
    }
}