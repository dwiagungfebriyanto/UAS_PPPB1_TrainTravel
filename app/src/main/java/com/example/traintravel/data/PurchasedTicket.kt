package com.example.traintravel.data

import java.io.Serializable

data class PurchasedTicket(
    var id: String = "",
    var userId: String = "",
    var ticketId: String = "",
    var totalPrice: Int = 0,
    var purchaseDate: String = "",
    var additionalPackages: List<String> = emptyList()
) : Serializable
