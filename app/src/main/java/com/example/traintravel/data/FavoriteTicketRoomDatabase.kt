package com.example.traintravel.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi

@Database(entities = [FavoriteTicket::class], version = 1, exportSchema = false)
abstract class FavoriteTicketRoomDatabase : RoomDatabase() {
    abstract fun favoriteTicketDuo(): FavoriteTicketDao?
    companion object {
        @Volatile
        private var INSTANCE: FavoriteTicketRoomDatabase? = null
        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context) : FavoriteTicketRoomDatabase? {
            synchronized(FavoriteTicketRoomDatabase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = databaseBuilder(context.applicationContext,
                        FavoriteTicketRoomDatabase::class.java, "favorite_ticket_database")
                        .build()
                }
            }
            return INSTANCE
        }
    }
}