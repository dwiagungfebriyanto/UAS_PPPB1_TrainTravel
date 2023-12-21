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
        // Memantau perubahan data dari Firebase
        Firebase.observeDataChanges()

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        prefManager = PrefManager.getInstance(requireContext())
        firebaseAuth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val authActivity = requireActivity() as AuthActivity

        with(binding) {
            // Beralih ke RegisterFragment ketika tombol "Register" diklik
            btnToRegister.setOnClickListener {
                authActivity.navigateToREgister()
            }

            // Melakukan proses login ketika tombol "Login" diklik
            btnLogin.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()

                // Menampilkan progress bar selama proses login
                authActivity.progressBarVisibility(true)

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    // Mencoba masuk dengan email dan password
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                        // Menyiapkan data pengguna setelah login berhasil
                        setLogin()
                    }.addOnFailureListener {
                        // Menampilkan pesan kesalahan saat login gagal
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Menampilkan pesan jika isian yang dibutuhkan tidak diisi
                    Toast.makeText(context, "Please fill out all required data!", Toast.LENGTH_SHORT).show()
                    authActivity.progressBarVisibility(false)
                }
            }
        }
    }

    private fun setLogin() {
        val userId = firebaseAuth.currentUser?.uid
        val authActivity = requireActivity() as AuthActivity

        // Mendapatkan data pengguna dari Firestore setelah login berhasil
        Firebase.usersCollectionRef.document(userId!!).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(Users::class.java)

                // Menyimpan data pengguna di SharedPreferences
                prefManager.saveUsername(user!!.username)
                prefManager.saveBirthDate(user.birthDate)
                prefManager.saveRole(user.role)
                prefManager.saveUserId(userId)
                prefManager.saveEmail(firebaseAuth.currentUser?.email!!)
                prefManager.setLoggedIn(true)

                // Memeriksa status login di activity utama
                authActivity.checkLoginState()
            }
        }.addOnFailureListener {
            // Mencatat pesan kesalahan dan menampilkan toast jika gagal
            Log.d("FirebaseAuth", "User data not found: ", it)
            Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT).show()
        }
    }
}