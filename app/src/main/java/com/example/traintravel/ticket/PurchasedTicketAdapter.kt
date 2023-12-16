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
                val ticket = Firebase.getTicket(purchasedTicket.ticketId)

                if (ticket != null) {
                    txtTrainName.text       = ticket.trainName
                    txtPrice.text           = NumberFormat.getNumberInstance(Locale("id")).format(purchasedTicket.totalPrice)
                    txtDeparture.text       = ticket.departureStation
                    txtDepartureTime.text   = ticket.departureTime
                    txtDestination.text     = ticket.destinationStation
                    txtArrivalTime.text     = ticket.arrivalTime
                    txtClass.text           = ticket.classType
                    txtDate.text            = getDate(ticket.departureDate)
                }

                itemView.setOnClickListener {
                    onClickPurchasedTicket(purchasedTicket)
                }

                icFavorite.visibility = View.GONE
                btnBuyTicket.visibility = View.GONE
                btnEditTicket.visibility = View.GONE
            }
        }
    }

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