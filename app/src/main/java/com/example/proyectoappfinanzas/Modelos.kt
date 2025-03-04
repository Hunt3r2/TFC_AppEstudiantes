package com.example.proyectoappfinanzas

class Modelos {
    data class Ingreso(
        val id: Int,
        val monto: Double,
        val descripcion: String,
        val categoria: String,
        val mes: Int,
        val anio: Int
    )

    data class Gasto(
        val id: Int,
        val monto: Double,
        val descripcion: String,
        val categoria: String,
        val mes: Int,
        val anio:Int
    )

    data class Categoria(
        val id: Int,
        val nombre: String
    )
}