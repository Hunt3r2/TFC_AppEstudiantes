package com.example.proyectoappfinanzas.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectoappfinanzas.modelos.Gasto

@Dao
interface GastoDao {
    @Insert
    suspend fun agregarGasto(gasto: Gasto)

    @Query("SELECT * FROM gastos WHERE mes = :mes AND anio = :anio")
    suspend fun obtenerGastosPorMesYAnio(mes: Int, anio: Int): List<Gasto>

    @Query("DELETE FROM gastos WHERE id = :id")
    suspend fun borrarGasto(id: Long)
}
