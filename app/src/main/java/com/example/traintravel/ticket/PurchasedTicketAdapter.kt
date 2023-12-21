package com.example.traintravel.ticket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.PurchasedTicket
import com.example.traintravel.databinding.ItemTicketBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

typealias OnClickPurchasedTicket = (PurchasedTicket) -> Unit

class PurchasedTicketAdapter (
    private val listPurchasedTicket: List<PurchasedTicket>,
    private val onClickPurchasedTicket: OnClickPurchasedTicket
) : RecyclerView.Adapter<PurchasedTicketAdapter.ItemTicketViewHolder>() {

    inner class ItemTicketViewHolder(private val binding: ItemTicketBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(purchasedTicket : PurchasedTicket) {
            with(binding) {
                // Mendapatkan informasi tiket dari Firebase berdasarkan ID tiket yang dibeli
                val ticket = Firebase.getTicket(purchasedTicket.ticketId)

                // Jika tiket ditemukan
                if (ticket != null) {
                    // Menetapkan informasi tiket ke tampilan
                    txtTrainName.text       = ticket.trainName
                    txtPrice.text           = NumberFormat.getNumberInstance(Locale("id")).format(purchasedTicket.totalPrice)
                    txtDeparture.text       = ticket.departureStation
                    txtDepartureTime.text   = ticket.departureTime
                    txtDestination.text     = ticket.destinationStation
                    txtArrivalTime.text     = ticket.arrivalTime
                    txtClass.text           = ticket.classType
                    txtDate.text            = getDate(ticket.departureDate)
                }

                // Menangani klik pada item tiket yang dibeli
                itemView.setOnClickListener {
                    onClickPurchasedTicket(purchasedTicket)
                }

                // Menyembunyikan ikon favorit dan tombol beli/edit tiket
                icFavorite.visibility = View.GONE
                btnBuyTicket.visibility = View.GONE
                btnEditTicket.visibility = View.GONE
            }
        }
    }

    // Mengonversi format tanggal
    private fun getDate(departuredDate : String) : String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val date: Date = inputFormat.parse(departuredDate) ?: Date()
        return outputFormat.format(date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTicketViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemTicketViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listPurchasedTicket.size
    }

    override fun onBindViewHolder(holder: ItemTicketViewHolder, position: Int) {
        holder.bind(listPurchasedTicket[position])
    }
}