package com.example.traintravel.user

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.traintravel.R
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.PurchasedTicket
import com.example.traintravel.databinding.FragmentPurchasedTicketDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PurchasedTicketDetailFragment(private val purchasedTicket : PurchasedTicket) : BottomSheetDialogFragment() {
    private var _binding: FragmentPurchasedTicketDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var confirmationTxt: TextView
    private lateinit var cancelTxt: TextView
    private lateinit var yesTxt: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPurchasedTicketDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listAdditionalPackages = purchasedTicket.additionalPackages
        val ticket = Firebase.getTicket(purchasedTicket.ticketId)

        with(binding) {
            val multi = MultiTransformation<Bitmap>(
                CenterCrop(),
                RoundedCornersTransformation(35, 0)
            )
            Glide.with(requireContext())
                .load(ticket?.imageUrl)
                .apply(RequestOptions.bitmapTransform(multi))
                .placeholder(R.drawable.img_placeholder)
                .into(imgStation)

            txtTrainName.text       = ticket?.trainName
            txtPrice.text           = NumberFormat.getNumberInstance(Locale("id")).format(purchasedTicket.totalPrice)
            txtDeparture.text       = ticket?.departureStation
            txtDepartureTime.text   = ticket?.departureTime
            txtDestination.text     = ticket?.destinationStation
            txtArrivalTime.text     = ticket?.arrivalTime
            txtClass.text           = ticket?.classType
            txtDepartureDate.text   = getDate(ticket!!.departureDate)
            txtTripDuration.text    = ticket.tripDuration
            txtPurchasedDate.text   = getDate(purchasedTicket.purchaseDate)

            if (listAdditionalPackages.isNotEmpty()) {
                txtEmptyPackages.visibility = View.GONE
                if (listAdditionalPackages.contains("Lunch box")) { extraLunchBox.visibility = View.VISIBLE }
                if (listAdditionalPackages.contains("Sit by the window")) { extraSitByTheWindow.visibility = View.VISIBLE }
                if (listAdditionalPackages.contains("Charging port")) { extraChargingPort.visibility = View.VISIBLE }
                if (listAdditionalPackages.contains("Wi-Fi")) { extraWifi.visibility = View.VISIBLE }
                if (listAdditionalPackages.contains("Smoking area")) { extraSmokingArea.visibility = View.VISIBLE }
                if (listAdditionalPackages.contains("Pillow & blanket")) { extraPillowAndBlanket.visibility = View.VISIBLE }
                if (listAdditionalPackages.contains("Extra luggage")) { extraExtraLuggage.visibility = View.VISIBLE }
                if (listAdditionalPackages.contains("Reclining seat")) { extraRecliningSeat.visibility = View.VISIBLE }
            }

            // Membuat objek Dialog untuk konfirmasi penghapusan
            val dialog = Dialog(requireContext())
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
                    Firebase.deletePurchasedTicket(purchasedTicket)
                    Firebase.observePurchasedTicket()
                    Toast.makeText(context, "Ticket successfully deleted", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    dismiss()
                }

                // Menampilkan dialog
                dialog.show()
            }

            btnClose.setOnClickListener {
                dismiss()
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