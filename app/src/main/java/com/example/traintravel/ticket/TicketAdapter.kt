package com.example.traintravel.ticket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.R
import com.example.traintravel.data.FavoriteTicket
import com.example.traintravel.data.Ticket
import com.example.traintravel.databinding.ItemTicketBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
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
                txtTrainName.text       = ticket.trainName
                txtPrice.text           = NumberFormat.getNumberInstance(Locale("id")).format(ticket.price)
                txtDeparture.text       = ticket.departureStation
                txtDepartureTime.text   = ticket.departureTime
                txtDestination.text     = ticket.destinationStation
                txtArrivalTime.text     = ticket.arrivalTime
                txtClass.text           = ticket.classType
                txtDate.text            = getDate(ticket.departureDate)

                itemView.setOnClickListener {
                    onClickTicket(ticket)
                }

                if (prefManager.isAdmin()) {
                    btnBuyTicket.visibility = View.GONE
                    icFavorite.visibility = View.GONE
                    btnEditTicket.setOnClickListener {
                        onClickButton(ticket)
                    }
                    if (adapterPosition == listTicket.size - 1) {
                        val params = itemView.layoutParams as RecyclerView.LayoutParams
                        params.bottomMargin = 250
                        itemView.layoutParams = params
                    }
                } else {
                    for (favoriteTicket in listFavoriteTicket!!) {
                        if (ticket.id == favoriteTicket.ticketId) {
                            icFavorite.setImageResource(R.drawable.baseline_bookmark_24)
                        }
                    }

                    btnBuyTicket.setOnClickListener {
                        onClickButton(ticket)
                    }

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

    private fun getDate(departuredDate : String) : String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val date: Date = inputFormat.parse(departuredDate) ?: Date()
        return outputFormat.format(date)
    }

    fun updateFavoriteList(newListFavoriteTicket: List<FavoriteTicket>?) {
        listFavoriteTicket = newListFavoriteTicket
    }
}