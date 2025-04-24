package com.example.easytableapp.modelo

data class Comensal_Producto(
    val IDComensal: Int,
    val IDProducto: Int,
    val cantidad: Int,
    var entregado: Boolean ?= false,
    val Notas: String
)
