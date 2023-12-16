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
            val user = Firebase.getUser(prefManager.getUsername())

            txtUsername.text = user?.username
            txtEmail.text = user?.email
            txtBirthdate.text = user?.birthDate
            txtRole.text = user?.role

            btnAddAdmin.setOnClickListener {
                AddAdminFragment().show(supportFragmentManager, "Add Admin")
            }

            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }
}