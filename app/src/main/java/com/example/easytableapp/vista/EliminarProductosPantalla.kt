package com.example.easytableapp.vista

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete


import com.example.easytableapp.controlador.ApiController
import com.example.easytableapp.modelo.Comensal
import com.example.easytableapp.modelo.Comensal_Producto
import com.example.easytableapp.modelo.Comensal_Producto_Extra
import com.example.easytableapp.modelo.Extra
import com.example.easytableapp.modelo.Producto
import com.example.easytableapp.ui.softGreen
import com.example.easytableapp.ui.softRed
import java.util.Locale


@Composable
fun EliminarProductosPantalla(idComensal: Int, navController: NavController) {
    // Flag para saber si se ha cargado la lista
    val isLoading = remember { mutableStateOf(true) }
    // Llamada a la API para obtener los productos del comensal
    var productos by remember { mutableStateOf(emptyList<Producto>()) }
    // Variable para formatear numeros en formato de moneda
    val numberFormat = java.text.NumberFormat.getNumberInstance(Locale.US)
    var extras by remember { mutableStateOf(emptyList<Extra>()) }
    var comensalProducto by remember { mutableStateOf(emptyList<Comensal_Producto>()) }
    var comensalProductoExtra by remember { mutableStateOf(emptyList<Comensal_Producto_Extra>()) }
    var comensal by remember { mutableStateOf<Comensal?>(null) }
    fun actualizarProductos() {
        ApiController.obtenerPedidosComensal(idComensal,
            { productosList, extrasList, comensalProductoList, comensalProductoExtraList ->
                productos = productosList
                extras = extrasList
                comensalProducto = comensalProductoList
                comensalProductoExtra = comensalProductoExtraList
                isLoading.value = false
            },
            {
                Log.e("API", "Error: ${it.localizedMessage}")
                isLoading.value = false
            })
    }
    LaunchedEffect(Unit) {
        // Solicitud para obtener productos
        actualizarProductos()

        ApiController.obtenerDatosComensal(idComensal,
            { comensal = it },
            { Log.e("API", "Error: ${it.localizedMessage}") })
    }

    // Contenedor principal
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Navbar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            ) {
                IconButton(onClick = { navController.navigate("detalles_mesa/${comensal?.IDMesa}") }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        }

        // Separador del navbar
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        // Contenedor de contenido
        Column(
            modifier = Modifier
                .padding(32.dp)
        ) {
            // Contenedor de titulo y subtitulo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Titulo de la pantalla
                Text(
                    text = "${comensal?.NombreComensal}",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
                // Subtitulo de la pantalla
                Text(
                    text = "Eliminar productos",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }
            // Contenedor de productos
            Column {
                if (isLoading.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando productos...",
                                fontSize = 20.sp,
                                color = Color.LightGray,
                            )
                        }
                    }
                } else if (productos.isEmpty()) {
                    // Mostrar mensaje de error si no hay datos
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No se encontraron productos",
                                fontSize = 20.sp,
                                color = Color.LightGray,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                } else {
                    LazyColumn {
                        items(comensalProducto) { pedido ->
                            val producto = productos.find { it.IDProducto == pedido.IDProducto }
                            val extrasPedido =
                                comensalProductoExtra.filter { it.IDComensal == pedido.IDComensal && it.IDProducto == pedido.IDProducto }

                            // Contenedor de cada producto
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                // Nombre del producto y cantidad
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${producto?.NombreProducto ?: "Desconocido"} (x${pedido.cantidad})",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Total: $${numberFormat.format(pedido.cantidad * (producto?.ValorProducto ?: 0))}",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )

                                    pedido.Notas?.let {
                                        ProductoConEliminar(producto, idComensal,
                                            it, pedido.cantidad, ::actualizarProductos)
                                    }
                                }

                                // Extras
                                extrasPedido.filter { it.cantidad >= 1 }.forEach { extraPedido ->
                                    val extra = extras.find { it.IDExtra == extraPedido.IDExtra }
                                    if (extra != null) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 16.dp)
                                        ) {
                                            Text(
                                                text = "➜ ${extra.NombreExtra} (x${extraPedido.cantidad})",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                            Text(
                                                text = "+$${numberFormat.format(extraPedido.cantidad * extra.ValorExtra)}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                        }
                                    }
                                }

                                // Notas (solo si existen)
                                pedido.Notas?.takeIf { it.isNotBlank() }?.let { nota ->
                                    Text(
                                        text = "📝 Nota: $nota",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Blue,
                                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoConEliminar(producto: Producto?, idComensal: Int, notas: String, cantidadDisponible: Int, actualizarProductos: () -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    var cantidadSeleccionada = remember { mutableIntStateOf(1) }

    if (showDialog.value) {
        cantidadSeleccionada = remember { mutableIntStateOf(1) }
        AlertDialog (
            onDismissRequest = { showDialog.value = false },
            title = { Text("Confirmación") },
            text = {
                Column {
                    if (producto != null) {
                        Text("¿Está seguro de que desea eliminar ${producto.NombreProducto} del pedido?")
                        Spacer(modifier = Modifier.height(16.dp))

                        // Sección de Cantidad
                        Text("Cantidad de Producto")
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { if (cantidadSeleccionada.intValue > 1) cantidadSeleccionada.intValue -= 1 },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.LightGray,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Text("-")
                                }
                                Text(cantidadSeleccionada.intValue.toString())
                                Button(
                                    onClick = { if (cantidadSeleccionada.intValue < cantidadDisponible) cantidadSeleccionada.intValue += 1 },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.LightGray,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Text("+")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (producto != null) {
                            ApiController.eliminarProductoDeComensalConCantidad(
                                producto.IDProducto,
                                idComensal,
                                notas,
                                cantidadSeleccionada.intValue,
                                {
                                    actualizarProductos()
                                    showDialog.value = false
                                },
                                {
                                    Log.e("API", "Error: ${it.localizedMessage}")
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = softGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = softRed,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Botón para abrir el diálogo
    Box(
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 8.dp)
    ) {
        Button(
            onClick = { showDialog.value = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = softRed,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon (
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar producto"
                )
            }
        }
    }
}