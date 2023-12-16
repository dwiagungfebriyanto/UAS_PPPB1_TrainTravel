package com.example.traintravel.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import java.io.Serializable

@Entity(tableName = "favorite_ticket_table")
data class FavoriteTicket(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Int = 0,

    @ColumnInfo(name = "ticket_id")
    val ticketId: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "train_name")
    val trainName: String,

    @ColumnInfo(name = "departure_date")
    val departureDate: String,

    @ColumnInfo(name = "price")
    val price: Int,

    @ColumnInfo(name = "class_type")
    val classType: String,

    @ColumnInfo(name = "departure_station")
    val departureStation: String,

    @ColumnInfo(name = "departure_time")
    val departureTime: String,

    @ColumnInfo(name = "destination_station")
    val destinationStation: String,

    @ColumnInfo(name = "arrival_time")
    val arrivalTime: String,

    @ColumnInfo(name = "trip_duration")
    val tripDuration: String
) : Serializable
