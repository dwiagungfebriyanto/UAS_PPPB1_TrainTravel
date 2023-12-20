package com.example.traintravel.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.traintravel.admin.DashboardActivity
import com.example.traintravel.databinding.ActivityAuthBinding
import com.example.traintravel.user.UserActivity
import com.google.android.material.tabs.TabLayoutMediator

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT
        }
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this)

        with(binding) {
            viewPager.adapter = TabAdapter(this@AuthActivity)
            viewPager2 = viewPager

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when(position) {
                    0 -> "REGISTER"
                    1 -> "LOGIN"
                    else -> ""
                }
            }.attach()

            scrollView.viewTreeObserver.addOnScrollChangedListener {
                if (scrollView.scrollY > 800 && rlWelcome.visibility == View.VISIBLE) {
                    rlWelcome.visibility = View.INVISIBLE
                } else if (scrollView.scrollY < 800 && rlWelcome.visibility == View.INVISIBLE)
                    rlWelcome.visibility = View.VISIBLE
            }
        }
    }

    fun navigateToLogin() {
        viewPager2.setCurrentItem(1, true)
    }

    fun navigateToREgister() {
        viewPager2.setCurrentItem(0, true)
    }

    fun checkLoginState() {
        if (prefManager.isLoggedIn()) {
            Toast.makeText(this@AuthActivity, "Login successful!", Toast.LENGTH_SHORT).show()
            progressBarVisibility(false)
            if (prefManager.getRole() == "user") {
                startActivity(Intent(this@AuthActivity, UserActivity::class.java))
            } else {
                startActivity(Intent(this@AuthActivity, DashboardActivity::class.java))
            }
            finish()
        }
    }

    fun progressBarVisibility(isVisible : Boolean) {
        with(binding) {
            if (isVisible) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.INVISIBLE
            }
        }
    }
}