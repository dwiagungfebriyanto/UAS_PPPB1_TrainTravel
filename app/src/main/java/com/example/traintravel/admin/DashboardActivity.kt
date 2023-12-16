package com.example.traintravel.admin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.auth.AuthActivity
import com.example.traintravel.R
import com.example.traintravel.data.Firebase
import com.example.traintravel.databinding.ActivityDashboardBinding
import com.example.traintravel.ticket.TicketAdapter

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDashboardBinding
    private lateinit var prefManager: PrefManager
    private lateinit var confirmationTxt: TextView
    private lateinit var cancelTxt: TextView
    private lateinit var yesTxt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefManager = PrefManager.getInstance(this)

        with(binding) {
            btnAddTicket.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, TicketFormActivity::class.java))
            }

            btnProfile.setOnClickListener {
                startActivity(
                    Intent(this@DashboardActivity, AdminProfileActivity::class.java)
                )
            }

            // Membuat objek Dialog untuk konfirmasi logout
            val dialog = Dialog(this@DashboardActivity)
            btnLogout.setOnClickListener {
                dialog.setContentView(R.layout.dialog_confirm)
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.setCancelable(false)
                dialog.window!!.attributes.gravity = Gravity.BOTTOM
                dialog.window!!.attributes.windowAnimations = R.style.animation
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                confirmationTxt = dialog.findViewById(R.id.txt_confirmation)
                cancelTxt = dialog.findViewById(R.id.txt_cancel)
                yesTxt = dialog.findViewById(R.id.txt_yes)

                confirmationTxt.text = "Are you sure want to logout?"

                // Menangani klik tombol Cancel pada dialog
                cancelTxt.setOnClickListener {
                    dialog.dismiss()
                }

                // Menangani klik tombol Yes pada dialog
                yesTxt.setOnClickListener {
                    prefManager.setLoggedIn(false)
                    startActivity(
                        Intent(this@DashboardActivity, AuthActivity::class.java)
                    )
                    finish()
                }

                // Menampilkan dialog
                dialog.show()
            }
        }

        observeTickets()
    }

    private fun observeTickets() {
        Firebase.ticketsListLiveData.observe(this@DashboardActivity) { tickets ->
            val adapterTicket = TicketAdapter(
                tickets, 
                { ticket -> 
                    Toast.makeText(this@DashboardActivity, ticket.trainName, Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(
                            this@DashboardActivity, TicketDetailActivity::class.java
                        ).putExtra("ticket", ticket)
                    )
                }, 
                { ticket ->
                    startActivity(Intent(this@DashboardActivity, TicketFormActivity::class.java).putExtra("ticket", ticket))
                })
            binding.progressBar.visibility = View.GONE
            // Menerapkan adapter ke RecyclerView
            binding.rvTicket.apply {
                adapter = adapterTicket
                layoutManager = LinearLayoutManager(this@DashboardActivity)
            }
        }
    }
}