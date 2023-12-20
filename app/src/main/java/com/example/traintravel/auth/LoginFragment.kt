package com.example.traintravel.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.Users
import com.example.traintravel.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Firebase.observeDataChanges()
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        prefManager = PrefManager.getInstance(requireContext())
        firebaseAuth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val authActivity = requireActivity() as AuthActivity

        with(binding) {
            btnToRegister.setOnClickListener {
                authActivity.navigateToREgister()
            }

            btnLogin.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()

                authActivity.progressBarVisibility(true)

                if (email.isNotEmpty() && password.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                        setLogin()
                    }.addOnFailureListener {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill out all required forms!", Toast.LENGTH_SHORT).show()
                    authActivity.progressBarVisibility(false)
                }
            }
        }
    }

    private fun setLogin() {
        val userId = firebaseAuth.currentUser?.uid
        val authActivity = requireActivity() as AuthActivity
        Firebase.usersCollectionRef.document(userId!!).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(Users::class.java)
                prefManager.saveUsername(user!!.username)
                prefManager.saveBirthDate(user.birthDate)
                prefManager.saveRole(user.role)
                prefManager.saveUserId(userId)
                prefManager.saveEmail(firebaseAuth.currentUser?.email!!)
                prefManager.setLoggedIn(true)
                authActivity.checkLoginState()
            }
        }.addOnFailureListener {
            Log.d("FirebaseAuth", "User data not found: ", it)
            Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT).show()
        }
    }
}