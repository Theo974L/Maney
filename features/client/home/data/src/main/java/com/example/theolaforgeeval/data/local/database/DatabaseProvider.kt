package com.example.theolaforgeeval.data.local.database

import AppDatabase
import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    fun create(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_db"
        ).build()
    }
}