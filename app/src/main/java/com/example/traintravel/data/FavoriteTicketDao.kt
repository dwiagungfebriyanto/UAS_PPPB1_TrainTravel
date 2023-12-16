package com.example.traintravel.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavoriteTicketDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavoriteTicket(favoriteTicket: FavoriteTicket)

    @Delete
    fun deleteFavoriteTicket(favoriteTicket: FavoriteTicket)

    @Query("SELECT * FROM favorite_ticket_table WHERE user_id = :userId")
    fun getFavoriteTicket(userId: String): List<FavoriteTicket>

    @Query("SELECT * FROM favorite_ticket_table WHERE user_id = :userId")
    fun getFavoriteTicketLiveData(userId: String): LiveData<List<FavoriteTicket>>
}