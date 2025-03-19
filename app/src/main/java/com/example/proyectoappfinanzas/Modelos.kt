package com.example.proyectoappfinanzas
import androidx.room.Entity
import androidx.room.PrimaryKey

class Modelos {

    @Entity(tableName = "ingresos")
    data class Ingreso(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val monto: Double,
        val descripcion: String,
        val categoria: String,
        val mes: Int,
        val anio: Int
    )

    @Entity(tableName = "gastos")
    data class Gasto(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val monto: Double,
        val descripcion: String,
        val categoria: String,
        val mes: Int,
        val anio: Int
    )

    data class Categoria(
        val id: Int,
        val nombre: String
    )
}