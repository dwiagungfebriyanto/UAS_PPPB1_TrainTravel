package com.example.traintravel.ticket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.R
import com.example.traintravel.data.FavoriteTicket
import com.example.traintravel.databinding.ItemTicketBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

typealias OnClickFavoriteTicket = (FavoriteTicket) -> Unit
typealias OnLongClickFavoriteTicket = (FavoriteTicket) -> Unit
typealias OnClickBuyTicket = (String) -> Unit

class FavoriteTicketAdapter (
    private val listFavoriteTicket : List<FavoriteTicket>,
    private val onClickFavoriteTicket: OnClickFavoriteTicket,
    private val onLongClickFavoriteTicket: OnLongClickFavoriteTicket,
    private val onClickBuyTicket: OnClickBuyTicket
) :RecyclerView.Adapter<FavoriteTicketAdapter.ItemTicketViewHolder>() {
    private lateinit var prefManager: PrefManager

    inner class ItemTicketViewHolder(private val binding: ItemTicketBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ticket : FavoriteTicket) {
            prefManager = PrefManager.getInstance(binding.root.context)

            with(binding) {
                icFavorite.setImageResource(R.drawable.baseline_bookmark_24)
                txtTrainName.text       = ticket.trainName
                txtPrice.text           = NumberFormat.getNumberInstance(Locale("id")).format(ticket.price)
                txtDeparture.text       = ticket.departureStation
                txtDepartureTime.text   = ticket.departureTime
                txtDestination.text     = ticket.destinationStation
                txtArrivalTime.text     = ticket.arrivalTime
                txtClass.text           = ticket.classType
                txtDate.text            = getDate(ticket.departureDate)

                itemView.setOnClickListener {
                    onClickFavoriteTicket(ticket)
                }

                itemView.setOnLongClickListener {
                    onLongClickFavoriteTicket(ticket)
                    true
                }

                btnBuyTicket.setOnClickListener {
                    onClickBuyTicket(ticket.ticketId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTicketViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  ItemTicketViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listFavoriteTicket.size
    }

    override fun onBindViewHolder(holder: ItemTicketViewHolder, position: Int) {
        holder.bind(listFavoriteTicket[position])
    }

    private fun getDate(departuredDate : String) : String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val date: Date = inputFormat.parse(departuredDate) ?: Date()
        return outputFormat.format(date)
    }
}