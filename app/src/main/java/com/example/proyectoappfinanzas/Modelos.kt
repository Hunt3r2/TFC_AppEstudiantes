package com.example.proyectoappfinanzas

class Modelos {
    data class Ingreso(
        val id: Int,
        val monto: Double,
        val descripcion: String,
        val categoria: String
    )

    data class Gasto(
        val id: Int,
        val monto: Double,
        val descripcion: String,
        val categoria: String
    )

    data class Categoria(
        val id: Int,
        val nombre: String
    )
}