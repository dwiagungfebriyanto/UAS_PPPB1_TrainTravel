package com.example.traintravel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.admin.DashboardActivity
import com.example.traintravel.auth.AuthActivity
import com.example.traintravel.data.Firebase
import com.example.traintravel.user.UserActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var prefManger: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        prefManger = PrefManager.getInstance(this)

        Handler().postDelayed({
            Log.d("SplashActivity", intent.action.toString())
            checkLoginStatus()
        }, 1500)

        Firebase.observeDataChanges()
    }

    private fun checkLoginStatus() {
        val isLoggedIn = prefManger.isLoggedIn()
        val role = prefManger.getRole()
        if (isLoggedIn && role == "admin") {
            startActivity(
                Intent(this@SplashActivity, DashboardActivity::class.java)
            )
        } else if (isLoggedIn) {
            if (intent.hasExtra("ticketId")) {
                startActivity(
                    Intent(this, UserActivity::class.java)
                        .putExtra("ticketId", intent.getStringExtra("ticketId"))
                )
            } else {
                startActivity(
                    Intent(this@SplashActivity, UserActivity::class.java)
                )
            }
        } else {
            startActivity(
                Intent(this@SplashActivity, AuthActivity::class.java)
            )
        }
        finish()
    }
}