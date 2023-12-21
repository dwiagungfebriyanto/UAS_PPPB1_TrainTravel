package com.example.traintravel.admin

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.Users
import com.example.traintravel.databinding.FragmentAddAdminBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class AddAdminFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentAddAdminBinding? = null
    private val binding get() = _binding!!
    private var selectedDate = ""
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddAdminBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        with(binding) {
            // Mengatur aksi untuk tombol menampilkan pemilih tanggal
            btnBirthdate.setOnClickListener {
                showDatePicker()
            }

            // Mengatur aksi untuk tombol registrasi admin baru
            btnRegister.setOnClickListener {
                val email = edtEmail.text.toString()
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()
                val newUser = Users(
                    role = "admin",
                    username = username,
                    birthDate = selectedDate
                )

                // Memeriksa apakah semua data yang diperlukan telah diisi
                if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && selectedDate.isNotEmpty()) {
                    // Membuat akun admin baru menggunakan Firebase Authentication
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                        val userId = firebaseAuth.currentUser?.uid
                        val userDocument = Firebase.usersCollectionRef.document(userId!!)
                        // Menyimpan informasi admin baru ke Firestore
                        userDocument.set(newUser).addOnSuccessListener {
                            Toast.makeText(context, "Successfully created an admin account!", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }.addOnFailureListener {
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Menampilkan pesan kesalahan jika data belum lengkap
                    Toast.makeText(context, "Please fill out all required data!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Menampilkan pemilih tanggal
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
            binding.btnBirthdate.setText(selectedDate)
        }, year, month, dayOfMonth)

        datePicker.show()
    }
}