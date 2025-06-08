package com.example.proyectoappfinanzas.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoappfinanzas.modelos.Pomodoro

@Dao
interface PomodoroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(pomodoro: Pomodoro)

    @Update
    suspend fun actualizar(pomodoro: Pomodoro)

    @Delete
    suspend fun eliminar(pomodoro: Pomodoro)

    @Query("SELECT * FROM pomodoros ORDER BY id DESC")
    suspend fun obtenerTodos(): List<Pomodoro>

    @Query("SELECT * FROM pomodoros WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Pomodoro?
}