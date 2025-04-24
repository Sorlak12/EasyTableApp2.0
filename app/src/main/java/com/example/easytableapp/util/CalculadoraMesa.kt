package com.example.easytableapp.util

import com.example.easytableapp.modelo.Comensal
import com.example.easytableapp.modelo.Comensal_Producto
import com.example.easytableapp.modelo.Producto
import com.example.easytableapp.modelo.Extra
import com.example.easytableapp.modelo.Comensal_Producto_Extra

object CalculadoraMesa {
    // Calcula el total de un comensal
    fun calcularTotalComensal(
        comensal: Comensal,
        pedidos: List<Comensal_Producto>,
        productos: List<Producto>,
        extras: List<Comensal_Producto_Extra>,
        listaExtras: List<Extra>
    ): Int {
        var total = 0
        // Iterar sobre los productos que pidió el comensal
        for (pedido in pedidos.filter { it.IDComensal == comensal.IDComensal }) {
            val producto = productos.find { it.IDProducto == pedido.IDProducto }
            if (producto != null) {
                total += producto.ValorProducto * pedido.cantidad
                // Agregar extras si existen
                for (extraPedido in extras.filter { it.IDComensal == pedido.IDComensal && it.IDProducto == pedido.IDProducto }) {
                    val extra = listaExtras.find { it.IDExtra == extraPedido.IDExtra }
                    if (extra != null) {
                        total += extra.ValorExtra * extraPedido.cantidad
                    }
                }
            }
        }

        return total
    }

    // Calcula el total de toda la mesa
    fun calcularTotalMesa(
        comensales: List<Comensal>,
        pedidos: List<Comensal_Producto>,
        productos: List<Producto>,
        extras: List<Comensal_Producto_Extra>,
        listaExtras: List<Extra>
    ): Int {
        return comensales.sumOf { comensal ->
            calcularTotalComensal(comensal, pedidos, productos, extras, listaExtras)
        }
    }
}