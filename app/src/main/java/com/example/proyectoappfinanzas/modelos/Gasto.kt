package com.example.proyectoappfinanzas.modelos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monto: Double,
    val descripcion: String,
    val categoria: String,
    val mes: Int,
    val anio: Int
)