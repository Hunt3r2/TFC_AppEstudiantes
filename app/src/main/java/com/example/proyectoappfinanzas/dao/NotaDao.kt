package com.example.proyectoappfinanzas.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoappfinanzas.modelos.Nota

@Dao
interface NotaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(nota: Nota)

    @Update
    suspend fun actualizar(nota: Nota)

    @Delete
    suspend fun eliminar(nota: Nota)

    @Query("SELECT * FROM notas ORDER BY fecha_creacion DESC")
    suspend fun obtenerTodas(): List<Nota>

    @Query("SELECT * FROM notas WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Nota?
}
