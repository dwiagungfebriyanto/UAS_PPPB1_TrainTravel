package com.example.traintravel.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.data.Firebase
import com.example.traintravel.databinding.ActivityAdminProfileBinding

class AdminProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminProfileBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefManager = PrefManager.getInstance(this)

        with(binding) {
            txtUsername.text = prefManager.getUsername()
            txtEmail.text = prefManager.getUserEmail()
            txtBirthdate.text = prefManager.getUserBirthdate()
            txtRole.text = prefManager.getRole()

            btnAddAdmin.setOnClickListener {
                AddAdminFragment().show(supportFragmentManager, "Add Admin")
            }

            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }
}