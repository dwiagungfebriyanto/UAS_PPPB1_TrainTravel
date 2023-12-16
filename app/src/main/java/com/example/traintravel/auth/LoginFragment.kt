package com.example.traintravel.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.traintravel.admin.DashboardActivity
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.Users
import com.example.traintravel.databinding.FragmentLoginBinding
import com.example.traintravel.user.UserActivity

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Firebase.observeDataChanges()
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        prefManager = PrefManager.getInstance(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnLogin.setOnClickListener {
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()

                val user = Firebase.getUser(username)

                if (user != null){
                    if (password == user.password) {
                        setLogin(user)
                        if (prefManager.isAdmin() && prefManager.isLoggedIn()) {
                            startActivity(Intent(context, DashboardActivity::class.java))
                        } else if (!prefManager.isAdmin() && prefManager.isLoggedIn()) {
                            Toast.makeText(context, "Halo user $username!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(context, UserActivity::class.java))
                        }
                    } else {
                        Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Username $username is not found!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setLogin(user : Users) {
        prefManager.saveUserId(user.id)
        prefManager.saveUsername(user.username)
        prefManager.savePassword(user.password)
        prefManager.saveRole(user.role)
        prefManager.setLoggedIn(true)
    }
}