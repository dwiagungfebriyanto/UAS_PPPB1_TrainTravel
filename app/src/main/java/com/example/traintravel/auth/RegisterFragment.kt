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
import java.util.Calendar

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var yearOfBirth = 0
    private var selectedDate = ""

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
        with(binding) {
            btnBirthdate.setOnClickListener {
                showDatePicker()
            }

            btnRegister.setOnClickListener {
                val email = edtEmail.text.toString()
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()
                val newUser = Users(
                    email = email,
                    username = username,
                    password = password,
                    birthDate = selectedDate
                )

                val usernameAvailable = Firebase.getUser(username)

                if (usernameAvailable == null) {
                    if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && selectedDate.isNotEmpty()) {
                        Firebase.addUser(newUser)
                        (requireActivity() as AuthActivity).viewPager2.setCurrentItem(1, true)
                    } else {
                        Toast.makeText(context, "Please fill out all required data!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Username $username is not available!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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