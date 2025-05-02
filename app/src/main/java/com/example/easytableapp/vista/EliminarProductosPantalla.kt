package com.example.easytableapp.vista

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.easytableapp.controlador.ApiController
import com.example.easytableapp.modelo.*
import com.example.easytableapp.ui.softGreen
import com.example.easytableapp.ui.softRed
import java.util.Locale
import java.net.URLEncoder

@Composable
fun EliminarProductosPantalla(idComensal: Int, navController: NavController) {
    val isLoading = remember { mutableStateOf(true) }
    var productos by remember { mutableStateOf(emptyList<Producto>()) }
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
        actualizarProductos()
        ApiController.obtenerDatosComensal(idComensal,
            { comensal = it },
            { Log.e("API", "Error: ${it.localizedMessage}") })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                IconButton(onClick = { navController.navigate("detalles_mesa/${comensal?.IDMesa}") }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        }

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        Column(modifier = Modifier.padding(32.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "${comensal?.NombreComensal}", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
                Text(text = "Eliminar productos", fontSize = 16.sp, modifier = Modifier.padding(bottom = 16.dp), fontWeight = FontWeight.Normal, color = Color.Gray)
            }

            Column {
                if (isLoading.value) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando productos...", fontSize = 20.sp, color = Color.LightGray)
                        }
                    }
                } else if (productos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No se encontraron productos", fontSize = 20.sp, color = Color.LightGray, fontWeight = FontWeight.Normal)
                        }
                    }
                } else {
                    LazyColumn {
                        items(comensalProducto) { pedido ->
                            val producto = productos.find { it.IDProducto == pedido.IDProducto }
                            val extrasPedido = comensalProductoExtra.filter {
                                it.IDComensal == pedido.IDComensal && it.IDProducto == pedido.IDProducto && it.Instancia == pedido.Instancia
                            }

                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("${producto?.NombreProducto ?: "Desconocido"} (x${pedido.cantidad})", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Text("Total: $${numberFormat.format(pedido.cantidad * (producto?.ValorProducto ?: 0))}", fontSize = 18.sp, modifier = Modifier.padding(start = 8.dp))

                                    pedido.Notas?.let {
                                        ProductoConEliminar(producto, idComensal, it, pedido.Instancia, pedido.cantidad, ::actualizarProductos)
                                    }
                                }

                                extrasPedido.filter { it.cantidad >= 1 }.forEach { extraPedido ->
                                    val extra = extras.find { it.IDExtra == extraPedido.IDExtra }
                                    if (extra != null) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                                        ) {
                                            Text("➜ ${extra.NombreExtra} (x${extraPedido.cantidad})", fontSize = 16.sp)
                                            Text("+$${numberFormat.format(extraPedido.cantidad * extra.ValorExtra)}", fontSize = 16.sp)
                                        }
                                    }
                                }

                                pedido.Notas?.takeIf { it.isNotBlank() }?.let { nota ->
                                    Text("📝 Nota: $nota", fontSize = 14.sp, color = Color.Blue, modifier = Modifier.padding(top = 4.dp, start = 16.dp))
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
fun ProductoConEliminar(producto: Producto?, idComensal: Int, notas: String, instancia: Int, cantidadDisponible: Int, actualizarProductos: () -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    var cantidadSeleccionada = remember { mutableIntStateOf(1) }

    if (showDialog.value) {
        cantidadSeleccionada = remember { mutableIntStateOf(1) }
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Confirmación") },
            text = {
                Column {
                    if (producto != null) {
                        Text("¿Está seguro de que desea eliminar ${producto.NombreProducto} del pedido?")
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cantidad de Producto")
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = { if (cantidadSeleccionada.intValue > 1) cantidadSeleccionada.intValue -= 1 }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black), shape = RoundedCornerShape(8.dp)) { Text("-") }
                            Text(cantidadSeleccionada.intValue.toString())
                            Button(onClick = { if (cantidadSeleccionada.intValue < cantidadDisponible) cantidadSeleccionada.intValue += 1 }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black), shape = RoundedCornerShape(8.dp)) { Text("+") }
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
                                instancia,
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
                    colors = ButtonDefaults.buttonColors(containerColor = softGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = softRed, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Box(modifier = Modifier.height(48.dp).padding(horizontal = 8.dp)) {
        Button(
            onClick = { showDialog.value = true },
            colors = ButtonDefaults.buttonColors(containerColor = softRed, contentColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar producto")
        }
    }
}
