package com.example.proyectoappfinanzas

import androidx.room.TypeConverter
import java.util.Date

class Convertidores {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateATimestamp(date: Date?): Long? = date?.time
}
