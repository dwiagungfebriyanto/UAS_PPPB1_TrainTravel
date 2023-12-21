package com.example.traintravel.ticket

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.traintravel.R
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.data.FavoriteTicket
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.PurchasedTicket
import com.example.traintravel.data.Ticket
import com.example.traintravel.databinding.FragmentTicketDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketDetailFragment(
    private val ticket: Ticket? = null,
    private val favoriteTicket: FavoriteTicket? = null
) : BottomSheetDialogFragment() {
    private var _binding : FragmentTicketDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private lateinit var confirmationTxt: TextView
    private lateinit var cancelTxt: TextView
    private lateinit var yesTxt: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTicketDetailBinding.inflate(inflater, container, false)
        prefManager = PrefManager.getInstance(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            if (ticket != null) {
                // Memuat gambar stasiun dengan efek pemotongan dan pembulatan sudut
                val multi = MultiTransformation<Bitmap>(
                    CenterCrop(),
                    RoundedCornersTransformation(35, 0)
                )
                Glide.with(requireContext())
                    .load(ticket.imageUrl)
                    .apply(RequestOptions.bitmapTransform(multi))
                    .placeholder(R.drawable.img_placeholder)
                    .into(imgStation)

                // Menetapkan informasi tiket ke tampilan
                txtTrainName.text       = ticket.trainName
                txtPrice.text           = NumberFormat.getNumberInstance(Locale("id")).format(ticket.price)
                txtDeparture.text       = ticket.departureStation
                txtDepartureTime.text   = ticket.departureTime
                txtDestination.text     = ticket.destinationStation
                txtArrivalTime.text     = ticket.arrivalTime
                txtClass.text           = ticket.classType
                txtDepartureDate.text   = getDate(ticket.departureDate)
                txtTripDuration.text    = ticket.tripDuration

                if (prefManager.isAdmin()) {
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
                            // Menghapus tiket dan menutup fragment
                            Firebase.deleteTicket(ticket)
                            dialog.dismiss()
                            dismiss()
                        }

                        // Menampilkan dialog
                        dialog.show()
                    }
                }
            } else if (favoriteTicket != null) {
                // Mengatur visibilitas gambar stasiun menjadi tidak terlihat
                imgStation.visibility = View.GONE

                // Menetapkan informasi tiket favorit ke tampilan
                txtTrainName.text       = favoriteTicket.trainName
                txtPrice.text           = NumberFormat.getNumberInstance(Locale("id")).format(favoriteTicket.price)
                txtDeparture.text       = favoriteTicket.departureStation
                txtDepartureTime.text   = favoriteTicket.departureTime
                txtDestination.text     = favoriteTicket.destinationStation
                txtArrivalTime.text     = favoriteTicket.arrivalTime
                txtClass.text           = favoriteTicket.classType
                txtDepartureDate.text   = getDate(favoriteTicket.departureDate)
                txtTripDuration.text    = favoriteTicket.tripDuration
            }

            // Menangani klik tombol Close
            btnClose.setOnClickListener {
                dismiss()
            }

            // Menyesuaikan visibilitas tombol Delete berdasarkan peran pengguna
            btnDelete.visibility = if (!prefManager.isAdmin()) View.GONE else View.VISIBLE
        }
    }

    // Mengonversi format tanggal
    private fun getDate(departuredDate : String) : String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val date: Date = inputFormat.parse(departuredDate) ?: Date()
        return outputFormat.format(date)
    }
}