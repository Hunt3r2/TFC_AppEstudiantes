package com.example.proyectoappfinanzas.dao

import androidx.room.*
import com.example.proyectoappfinanzas.modelos.Flashcard

@Dao
interface FlashcardsDao {
    @Insert
    suspend fun insertar(flashcard: Flashcard)

    @Update
    suspend fun actualizar(flashcard: Flashcard)

    @Delete
    suspend fun eliminar(flashcard: Flashcard)

    @Query("SELECT * FROM flashcards")
    suspend fun obtenerTodas(): List<Flashcard>

    @Query("SELECT * FROM flashcards WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Flashcard?

}
