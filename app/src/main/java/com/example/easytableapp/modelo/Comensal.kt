package com.example.easytableapp.modelo

data class Comensal(
    val IDComensal: Int,
    val NombreComensal: String,
    val IDMesa: Int,
    val Pagado: Int ?= 0
)
