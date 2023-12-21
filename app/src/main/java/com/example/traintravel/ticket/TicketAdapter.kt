package com.example.traintravel.ticket

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.R
import com.example.traintravel.data.FavoriteTicket
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.Ticket
import com.example.traintravel.databinding.ItemTicketBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

typealias OnClickTicket = (Ticket) -> Unit
typealias OcClickButton = (Ticket) -> Unit
typealias OnLongClick = (Ticket) -> Unit

class TicketAdapter (
    private val listTicket: List<Ticket>,
    private val onClickTicket: OnClickTicket,
    private val onClickButton: OcClickButton,
    private val onLongClick: OnLongClick? = null,
    private var listFavoriteTicket: List<FavoriteTicket>? = null
) : RecyclerView.Adapter<TicketAdapter.ItemTicketViewHolder>() {
    private lateinit var prefManager: PrefManager

    inner class ItemTicketViewHolder(private val binding: ItemTicketBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ticket : Ticket) {
            prefManager = PrefManager.getInstance(binding.root.context)

            with(binding) {
                // Menetapkan informasi tiket ke tampilan
                txtTrainName.text       = ticket.trainName
                txtPrice.text           = NumberFormat.getNumberInstance(Locale("id")).format(ticket.price)
                txtDeparture.text       = ticket.departureStation
                txtDepartureTime.text   = ticket.departureTime
                txtDestination.text     = ticket.destinationStation
                txtArrivalTime.text     = ticket.arrivalTime
                txtClass.text           = ticket.classType
                txtDate.text            = getDate(ticket.departureDate)

                // Menangani klik pada item tiket
                itemView.setOnClickListener {
                    onClickTicket(ticket)
                }

                // Menyesuaikan tampilan berdasarkan peran pengguna (Admin atau Pengguna Biasa)
                if (prefManager.isAdmin()) {
                    btnBuyTicket.visibility = View.GONE
                    icFavorite.visibility = View.GONE

                    // Menangani klik tombol edit tiket oleh Admin
                    btnEditTicket.setOnClickListener {
                        onClickButton(ticket)
                    }

                    // Mengubah warna latar belakang kartu jika tanggal keberangkatan sudah lewat
                    if (Firebase.convertStringToDate(ticket.departureDate) < Calendar.getInstance().time) {
                        card.setCardBackgroundColor(Color.parseColor("#33F44336"))
                    }

                } else {
                    // Menampilkan ikon favorit berdasarkan status tiket di daftar favorit pengguna
                    for (favoriteTicket in listFavoriteTicket!!) {
                        if (ticket.id == favoriteTicket.ticketId) {
                            icFavorite.setImageResource(R.drawable.baseline_bookmark_24)
                        }
                    }

                    // Menangani klik tombol beli tiket oleh Pengguna Biasa
                    btnBuyTicket.setOnClickListener {
                        onClickButton(ticket)
                    }

                    // Menangani klik tahan pada item tiket oleh Pengguna Biasa
                    itemView.setOnLongClickListener {
                        onLongClick!!(ticket)
                        true
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTicketViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemTicketViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTicket.size
    }

    override fun onBindViewHolder(holder: ItemTicketViewHolder, position: Int) {
        holder.bind(listTicket[position])
    }

    // Mengonversi format tanggal
    private fun getDate(departuredDate : String) : String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val date: Date = inputFormat.parse(departuredDate) ?: Date()
        return outputFormat.format(date)
    }

    // Memperbarui daftar tiket favorit
    fun updateFavoriteList(newListFavoriteTicket: List<FavoriteTicket>?) {
        listFavoriteTicket = newListFavoriteTicket
    }
}