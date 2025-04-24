package com.example.easytableapp.modelo

data class Comensal_Producto_Extra(
    val IDComensal: Int,
    val IDProducto: Int,
    val Notas: String, // 🔑 clave obligatoria para coincidir con comensal_producto
    val IDExtra: Int,
    val cantidad: Int
)
