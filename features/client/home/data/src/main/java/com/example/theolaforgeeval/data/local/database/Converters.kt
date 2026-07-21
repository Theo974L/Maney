package com.example.theolaforgeeval.data.local.database

import androidx.room.TypeConverter
import com.example.theolaforgeeval.model.RecurrenceFrequency

class Converters {

    @TypeConverter
    fun fromFrequency(value: RecurrenceFrequency): String = value.name

    @TypeConverter
    fun toFrequency(value: String): RecurrenceFrequency = RecurrenceFrequency.valueOf(value)
}
