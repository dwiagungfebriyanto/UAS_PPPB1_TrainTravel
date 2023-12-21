package com.example.traintravel.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.Users
import com.example.traintravel.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var age = 0
    private var selectedDate = ""
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        val authActivity = requireActivity() as AuthActivity

        with(binding) {
            // Navigasi ke layar login ketika tombol "Login" diklik
            btnToLogin.setOnClickListener {
                authActivity.navigateToLogin()
            }

            // Menampilkan dialog pemilih tanggal ketika tombol "Birthdate" diklik
            btnBirthdate.setOnClickListener {
                showDatePicker()
            }

            // Melakukan registrasi pengguna ketika tombol "Register" diklik
            btnRegister.setOnClickListener {
                authActivity.progressBarVisibility(true)

                val email = edtEmail.text.toString()
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()
                val newUser = Users(
                    username = username,
                    birthDate = selectedDate
                )

                if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && selectedDate.isNotEmpty()) {
                    // Memeriksa apakah pengguna sudah berusia 18 tahun atau lebih
                    if (age >= 18) {
                        // Mencoba membuat pengguna baru di Firebase Authentication
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                            val userId = firebaseAuth.currentUser?.uid
                            val userDocument = Firebase.usersCollectionRef.document(userId!!)

                            // Menyimpan data pengguna ke Firestore
                            userDocument.set(newUser).addOnSuccessListener {
                                Toast.makeText(context, "Data saved! Please login!", Toast.LENGTH_SHORT).show()
                                authActivity.navigateToLogin()
                                authActivity.progressBarVisibility(false)
                                resetField()
                            }.addOnFailureListener {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Menampilkan pesan jika pengguna belum cukup umur untuk mendaftar
                        Toast.makeText(context, "You are not old enough to register!", Toast.LENGTH_SHORT).show()
                        authActivity.progressBarVisibility(false)
                    }
                } else {
                    // Menampilkan pesan jika formulir belum diisi lengkap
                    Toast.makeText(context, "Please fill out all required data!", Toast.LENGTH_SHORT).show()
                    authActivity.progressBarVisibility(false)
                }
            }
        }
    }

    // Mereset nilai pada input field
    private fun resetField() {
        with(binding) {
            edtEmail.setText("")
            edtUsername.setText("")
            edtPassword.setText("")
            btnBirthdate.text = ""
        }
    }

    // Menampilkan dialog pemilih tanggal
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val yearNow = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            // Menghitung usia pengguna berdasarkan tanggal lahir yang dipilih
            age = yearNow - year
            selectedDate = "$dayOfMonth/${month + 1}/$year"
            binding.btnBirthdate.text = selectedDate
        }, yearNow, month, dayOfMonth)

        datePicker.show()
    }
}