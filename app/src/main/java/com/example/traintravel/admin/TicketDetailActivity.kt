package com.example.traintravel.admin

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.traintravel.R
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.Ticket
import com.example.traintravel.databinding.ActivityTicketDetailBinding
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityTicketDetailBinding
    private lateinit var confirmationTxt: TextView
    private lateinit var cancelTxt: TextView
    private lateinit var yesTxt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            val ticket = intent.getSerializableExtra("ticket") as Ticket

            txtTrainName.text       = ticket.trainName
            txtPrice.text           = NumberFormat.getNumberInstance(Locale("id")).format(ticket.price)
            txtDeparture.text       = ticket.departureStation
            txtDepartureTime.text   = ticket.departureTime
            txtDestination.text     = ticket.destinationStation
            txtArrivalTime.text     = ticket.arrivalTime
            txtClass.text           = ticket.classType
            txtDepartureDate.text   = getDate(ticket.departureDate)
            txtTripDuration.text    = ticket.tripDuration

            if (ticket.imageUrl != "") {
                val multi = MultiTransformation<Bitmap>(
                    CenterCrop(),
                    RoundedCornersTransformation(35, 0, RoundedCornersTransformation.CornerType.TOP)
                )
                Glide.with(this@TicketDetailActivity).load(ticket.imageUrl)
                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(imgStation)
            }

            // Membuat objek Dialog untuk konfirmasi penghapusan
            val dialog = Dialog(this@TicketDetailActivity)
            // Menangani klik tombol Delete
            btnDelete.setOnClickListener {
                // Menampilkan dialog konfirmasi penghapusan
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

                confirmationTxt.text = "Delete this ticket?"

                // Menangani klik tombol Cancel pada dialog
                cancelTxt.setOnClickListener {
                    dialog.dismiss()
                }

                // Menangani klik tombol Yes pada dialog
                yesTxt.setOnClickListener {
                    // Menghapus kontak dan menutup activity
                    Firebase.deleteTicket(ticket)
                    finish()
                }

                // Menampilkan dialog
                dialog.show()
            }

            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun getDate(departuredDate : String) : String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val date: Date = inputFormat.parse(departuredDate) ?: Date()
        return outputFormat.format(date)
    }
}