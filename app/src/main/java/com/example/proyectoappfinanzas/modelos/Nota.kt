package com.example.proyectoappfinanzas.modelos

import androidx.room.*
import java.util.*

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val fecha_creacion: Date
)