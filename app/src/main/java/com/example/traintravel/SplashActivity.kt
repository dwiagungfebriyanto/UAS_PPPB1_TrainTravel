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

// Activity yang ditampilkan saat aplikasi pertama kali dijalankan
class SplashActivity : AppCompatActivity() {
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        prefManager = PrefManager.getInstance(this)

        // Menjalankan pengecekan status login setelah jeda waktu 1500ms (1.5 detik)
        Handler().postDelayed({
            checkLoginStatus()
        }, 1500)

        // Mengamati perubahan data dari Firebase
        Firebase.observeDataChanges()
    }

    // Memeriksa status login pengguna dan mengarahkannya ke layar yang sesuai
    private fun checkLoginStatus() {
        // Mendapatkan informasi apakah pengguna sudah login dan peran pengguna (admin atau pengguna biasa)
        val isLoggedIn = prefManager.isLoggedIn()
        val role = prefManager.getRole()

        // Mengecek status login dan mengarahkan pengguna ke layar yang sesuai
        if (isLoggedIn && role == "admin") {
            // Jika pengguna adalah admin, arahkan ke DashboardActivity
            startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
        } else if (isLoggedIn) {
            // Jika pengguna adalah pengguna biasa
            if (intent.hasExtra("ticketId")) {
                // Jika terdapat extra "ticketId" dalam intent, arahkan ke UserActivity dengan membawa extra "ticketId"
                startActivity(
                    Intent(this, UserActivity::class.java)
                        .putExtra("ticketId", intent.getStringExtra("ticketId"))
                )
            } else {
                // Jika tidak terdapat extra "ticketId" dalam intent, arahkan ke UserActivity
                startActivity(Intent(this@SplashActivity, UserActivity::class.java))
            }
        } else {
            // Jika pengguna belum login, arahkan ke AuthActivity
            startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
        }
        finish()
    }
}