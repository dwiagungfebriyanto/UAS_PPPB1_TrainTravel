package com.example.traintravel.data

import java.io.Serializable
import java.sql.Time

data class Ticket(
    var id: String = "",
    var trainName: String = "",
    var imageUrl: String = "",
    var departureDate: String = "",
    var price: Int = 0,
    var classType: String = "",
    var departureStation: String = "",
    var departureTime: String = "",
    var destinationStation: String = "",
    var arrivalTime: String = "",
    var tripDuration: String = ""
) : Serializable
