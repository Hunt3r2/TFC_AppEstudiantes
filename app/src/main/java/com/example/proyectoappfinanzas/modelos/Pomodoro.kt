package com.example.proyectoappfinanzas.modelos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoros")
data class Pomodoro(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val descripcion: String,
    val tiempo_trabajo: Int,
    val tiempo_descanso: Int,
    val tiempo_pausa_larga: Int,
    val repetir_pomodoro: Int
)