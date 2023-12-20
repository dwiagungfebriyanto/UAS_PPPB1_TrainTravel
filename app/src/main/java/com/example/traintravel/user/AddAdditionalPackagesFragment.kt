package com.example.traintravel.user

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.traintravel.auth.PrefManager
import com.example.traintravel.R
import com.example.traintravel.SplashActivity
import com.example.traintravel.ticket.TicketPricing
import com.example.traintravel.data.Firebase
import com.example.traintravel.data.PurchasedTicket
import com.example.traintravel.data.Ticket
import com.example.traintravel.databinding.FragmentAddAdditionalPackagesBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddAdditionalPackagesFragment(private val ticket: Ticket) : BottomSheetDialogFragment() {
    private var _binding: FragmentAddAdditionalPackagesBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private var totalPrice = 0
    private var activePackages = mutableSetOf<String>()
    private val channelId = "SUCCESSFUL_PURCHASE"
    private var notifId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddAdditionalPackagesBinding.inflate(inflater, container, false)
        prefManager = PrefManager.getInstance(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notifManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(binding) {
            txtPrice.text = NumberFormat.getNumberInstance(Locale("id")).format(ticket.price)

            toggleLunchBox.setOnCheckedChangeListener { _, isChecked ->
                if (toggleLunchBox.isChecked) {
                    activePackages.add(toggleLunchBox.textOn.toString())
                } else {
                    activePackages.remove(toggleLunchBox.textOff.toString())
                }
                updateTotalPrice()
            }

            toggleSitByTheWindow.setOnCheckedChangeListener { _, isChecked ->
                if (toggleSitByTheWindow.isChecked) {
                    activePackages.add(toggleSitByTheWindow.textOn.toString())
                } else {
                    activePackages.remove(toggleSitByTheWindow.textOff.toString())
                }
                updateTotalPrice()
            }

            toggleChargingPort.setOnCheckedChangeListener { _, isChecked ->
                if (toggleChargingPort.isChecked) {
                    activePackages.add(toggleChargingPort.textOn.toString())
                } else {
                    activePackages.remove(toggleChargingPort.textOff.toString())
                }
                updateTotalPrice()
            }

            toggleWifi.setOnCheckedChangeListener { _, isChecked ->
                if (toggleWifi.isChecked) {
                    activePackages.add(toggleWifi.textOn.toString())
                } else {
                    activePackages.remove(toggleWifi.textOff.toString())
                }
                updateTotalPrice()
            }

            toggleSmokingArea.setOnCheckedChangeListener { _, isChecked ->
                if (toggleSmokingArea.isChecked) {
                    activePackages.add(toggleSmokingArea.textOn.toString())
                } else {
                    activePackages.remove(toggleSmokingArea.textOff.toString())
                }
                updateTotalPrice()
            }

            togglePillowAndBlanket.setOnCheckedChangeListener { _, isChecked ->
                if (togglePillowAndBlanket.isChecked) {
                    activePackages.add(togglePillowAndBlanket.textOn.toString())
                } else {
                    activePackages.remove(togglePillowAndBlanket.textOff.toString())
                }
                updateTotalPrice()
            }

            toggleExtraLuggage.setOnCheckedChangeListener { _, isChecked ->
                if (toggleExtraLuggage.isChecked) {
                    activePackages.add(toggleExtraLuggage.textOn.toString())
                } else {
                    activePackages.remove(toggleExtraLuggage.textOff.toString())
                }
                updateTotalPrice()
            }

            toggleRecliningSeat.setOnCheckedChangeListener { _, isChecked ->
                if (toggleRecliningSeat.isChecked) {
                    activePackages.add(toggleRecliningSeat.textOn.toString())
                } else {
                    activePackages.remove(toggleRecliningSeat.textOff.toString())
                }
                updateTotalPrice()
            }

            btnBuyTicket.setOnClickListener {
                val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                val purchasedTicket = PurchasedTicket(
                    userId = prefManager.getUserId(),
                    ticketId = ticket.id,
                    totalPrice = if (totalPrice == 0) ticket.price else totalPrice,
                    purchaseDate = dateFormat.format(Date()),
                    additionalPackages = activePackages.toList()
                )
                val isAdded = Firebase.addPurchasedTicket(purchasedTicket)
                if (isAdded) {
                    Toast.makeText(context, "You have purchased this ticket!", Toast.LENGTH_SHORT).show()
                } else {
                    val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.FLAG_MUTABLE
                    } else { 0 }

                    val notificationId = System.currentTimeMillis().toInt() + notifId
                    notifId++

                    val intent = Intent(requireContext(), SplashActivity::class.java)
                        .putExtra("ticketId", ticket.id)
                    val pendingIntent = PendingIntent.getActivity(
                        requireContext(), 0, intent, flag
                    )

                    val builder = NotificationCompat.Builder(requireContext(), channelId)
                        .setSmallIcon(R.drawable.baseline_directions_transit_filled_24)
                        .setContentTitle("Ticket purchase successful!")
                        .setContentText("Expand to see ticket details")
                        .setStyle(
                            NotificationCompat.InboxStyle()
                                .addLine("User: ${prefManager.getUsername()}")
                                .addLine("Total price: ${if (totalPrice == 0) ticket.price else totalPrice}")
                                .addLine("Train name: ${ticket.trainName}")
                                .addLine("Departure date: ${ticket.departureDate}")
                                .addLine("Departure station: ${ticket.departureStation}")
                                .addLine("Destination station: ${ticket.destinationStation}")
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .addAction(0, "Purchased Ticket Detail", pendingIntent)

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                        val notifChannel = NotificationChannel(channelId, "Successful Purchase", NotificationManager.IMPORTANCE_DEFAULT)
                        with(notifManager) {
                            createNotificationChannel(notifChannel)
                            notify(notificationId, builder.build())
                        }
                    } else {
                        notifManager.notify(notificationId, builder.build())
                    }
                    dismiss()
                }
            }
        }
    }

    private fun updateTotalPrice() {
        var packagesPrice = 0

        for (packageName in activePackages) {
            packagesPrice += TicketPricing.additionalPackages[packageName] ?: 0
        }

        totalPrice = packagesPrice + ticket.price
        binding.txtPrice.text = NumberFormat.getNumberInstance(Locale("id")).format(totalPrice)
    }
}