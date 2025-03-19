package com.example.proyectoappfinanzas.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectoappfinanzas.modelos.Ingreso

@Dao
interface IngresoDao {
    @Insert
    suspend fun agregarIngreso(ingreso: Ingreso)

    @Query("SELECT * FROM ingresos WHERE mes = :mes AND anio = :anio")
    suspend fun obtenerIngresosPorMesYAnio(mes: Int, anio: Int): List<Ingreso>

    @Query("DELETE FROM ingresos WHERE id = :id")
    suspend fun borrarIngreso(id: Long)
}
