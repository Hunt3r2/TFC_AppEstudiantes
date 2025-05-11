package com.example.proyectoappfinanzas.modelos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pregunta: String,
    val respuesta: String
)

