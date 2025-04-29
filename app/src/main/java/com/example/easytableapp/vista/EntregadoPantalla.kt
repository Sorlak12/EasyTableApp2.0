package com.example.easytableapp.vista

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@Composable
fun EntregadoPantalla(navController: NavController, idMesa: Int) {
    // Flag para saber si se ha cargado la lista
    val isLoading = remember { mutableStateOf(true) }
    // Lista de comensales, productos, extras y comensal_producto
    var comensales by remember { mutableStateOf(emptyList<Comensal?>()) }
    var productos by remember { mutableStateOf(emptyList<Producto>()) }
    var extras by remember { mutableStateOf(emptyList<Extra>()) }
    var comensalProducto by remember { mutableStateOf(emptyList<Comensal_Producto>()) }
    var comensalProductoExtra by remember { mutableStateOf(emptyList<Comensal_Producto_Extra>()) }
    var showSaveButton by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Force recomposition when checkbox is toggled
    var trigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        ApiController.obtenerDetallesMesa(idMesa,
            { comensalesList, productosList, extrasList, comensalProductoList, comensalProductoExtraList ->
                comensales = comensalesList
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

    val groupedProductos = comensalProducto
        .groupBy { pedido ->
            val extrasKey = comensalProductoExtra
                .filter { it.IDComensal == pedido.IDComensal && it.IDProducto == pedido.IDProducto && it.Instancia == pedido.Instancia }
                .sortedBy { it.IDExtra }
                .joinToString("-") { "${it.IDExtra}:${it.cantidad}" }

            Triple(pedido.IDComensal, pedido.IDProducto, "${pedido.Notas.orEmpty()}#${pedido.Instancia}")

        }
        .map { (_, pedidos) ->
            val first = pedidos.first()
            val cantidadTotal = pedidos.sumOf { it.cantidad }
            first.copy(cantidad = cantidadTotal, entregado = pedidos.all { it.entregado == true })
        }

    // Contenedor principal
    Column (
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Navbar
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon (
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        }

        // Separador del navbar
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        // Contenedor de contenido
        Column (
            modifier = Modifier
                .padding(32.dp)
        ) {
            // Contenedor de titulo
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text (
                    text = "Productos Entregados",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text (
                    text = "Marcar productos como entregados",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Contenedor de productos
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                if (isLoading.value) {
                    Box (
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(20.dp))
                            Text (
                                text = "Cargando productos...",
                                fontSize = 20.sp,
                                color = Color.LightGray,
                            )
                        }
                    }
                } else if (groupedProductos.isEmpty()) {
                    // Mostrar mensaje de error si no hay datos
                    Box (
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text (
                            text = "No se encontraron productos",
                            fontSize = 20.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Normal
                        )
                    }
                } else {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                    ) {
                        LazyColumn {
                            // Productos no entregados
                            item {
                                Text (
                                    text = "Productos no entregados",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
                            }

                            items(groupedProductos.filter { it.entregado == false }) { pedidoAgrupado ->
                                val producto = productos.find { it.IDProducto == pedidoAgrupado.IDProducto }

                                val extrasDelProducto = comensalProductoExtra.filter {
                                    it.IDProducto == pedidoAgrupado.IDProducto &&
                                            it.IDComensal == pedidoAgrupado.IDComensal &&
                                            it.Instancia == pedidoAgrupado.Instancia
                                }


                                Column (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text (
                                            text = "${producto?.NombreProducto ?: "Desconocido"} (x${pedidoAgrupado.cantidad})",
                                            fontSize = 18.sp,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Checkbox (
                                            checked = pedidoAgrupado.entregado ?: false,
                                            onCheckedChange = { isChecked ->
                                                Log.d("Checkbox", "Changed to: $isChecked")

                                                val extrasKeyTarget = extrasDelProducto
                                                    .sortedBy { it.IDExtra }
                                                    .joinToString("-") { "${it.IDExtra}:${it.cantidad}" }

                                                val updatedList = comensalProducto.map {
                                                    val currentExtrasKey = comensalProductoExtra
                                                        .filter { extra -> extra.IDProducto == it.IDProducto && extra.IDComensal == it.IDComensal && extra.Instancia == it.Instancia }
                                                        .sortedBy { extra -> extra.IDExtra }
                                                        .joinToString("-") { extra -> "${extra.IDExtra}:${extra.cantidad}" }

                                                    if (
                                                        it.IDComensal == pedidoAgrupado.IDComensal &&
                                                        it.IDProducto == pedidoAgrupado.IDProducto &&
                                                        it.Notas.orEmpty() == pedidoAgrupado.Notas.orEmpty() &&
                                                        currentExtrasKey == extrasKeyTarget
                                                    ) {
                                                        it.copy(entregado = isChecked)
                                                    } else it
                                                }

                                                comensalProducto = updatedList
                                                showSaveButton = true
                                                trigger++ // Force recomposition
                                            },
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    // Contenedor de extras y notas
                                    Column (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp)
                                    ) {
                                        extrasDelProducto.forEach { extraPedido ->
                                            val extra = extras.find { it.IDExtra == extraPedido.IDExtra }
                                            extra?.takeIf { extraPedido.cantidad >= 1 }?.let {
                                                Text (
                                                    text = "➜ ${extra.NombreExtra} (x${extraPedido.cantidad})",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            }
                                        }

                                        pedidoAgrupado.Notas?.takeIf { it.isNotBlank() }?.let { nota ->
                                            Text(
                                                text = "📝 Nota: $nota",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color.Blue,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Productos entregados
                            item {
                                Text(
                                    text = "Productos entregados",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                                HorizontalDivider(thickness = 1.dp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
                            }

                            items(groupedProductos.filter { it.entregado == true }) { pedidoAgrupado ->
                                val producto = productos.find { it.IDProducto == pedidoAgrupado.IDProducto }

                                val extrasDelProducto = comensalProductoExtra.filter {
                                    it.IDProducto == pedidoAgrupado.IDProducto &&
                                            it.IDComensal == pedidoAgrupado.IDComensal &&
                                            it.Instancia == pedidoAgrupado.Instancia
                                }

                                Column (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text (
                                            text = "${producto?.NombreProducto ?: "Desconocido"} (x${pedidoAgrupado.cantidad})",
                                            fontSize = 18.sp
                                        )

                                        Checkbox (
                                            checked = pedidoAgrupado.entregado ?: false,
                                            onCheckedChange = { isChecked ->
                                                Log.d("Checkbox", "Changed to: $isChecked")

                                                val extrasKeyTarget = extrasDelProducto
                                                    .sortedBy { it.IDExtra }
                                                    .joinToString("-") { "${it.IDExtra}:${it.cantidad}" }

                                                val updatedList = comensalProducto.map {
                                                    val currentExtrasKey = comensalProductoExtra
                                                        .filter { extra -> extra.IDProducto == it.IDProducto && extra.IDComensal == it.IDComensal && extra.Instancia == it.Instancia }
                                                        .sortedBy { extra -> extra.IDExtra }
                                                        .joinToString("-") { extra -> "${extra.IDExtra}:${extra.cantidad}" }

                                                    if (
                                                        it.IDComensal == pedidoAgrupado.IDComensal &&
                                                        it.IDProducto == pedidoAgrupado.IDProducto &&
                                                        it.Notas.orEmpty() == pedidoAgrupado.Notas.orEmpty() &&
                                                        currentExtrasKey == extrasKeyTarget
                                                    ) {
                                                        it.copy(entregado = isChecked)
                                                    } else it
                                                }

                                                comensalProducto = updatedList
                                                showSaveButton = true
                                                trigger++ // Force recomposition
                                            },
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    // Contenedor de extras y notas
                                    Column (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp)
                                    ) {
                                        extrasDelProducto.forEach { extraPedido ->
                                            val extra = extras.find { it.IDExtra == extraPedido.IDExtra }
                                            extra?.takeIf { extraPedido.cantidad >= 1 }?.let {
                                                Text (
                                                    text = "➜ ${extra.NombreExtra} (x${extraPedido.cantidad})",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            }
                                        }

                                        pedidoAgrupado.Notas?.takeIf { it.isNotBlank() }?.let { nota ->
                                            Text(
                                                text = "📝 Nota: $nota",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color.Blue,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (showSaveButton) {
                        Column (
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Button (
                                onClick = {
                                    ApiController.actualizarEntregado(
                                        comensalProducto,

                                        onSuccess = { success ->
                                            if (success) {
                                                showSaveButton = false
                                                showSuccessDialog = true
                                            } else {
                                                showErrorDialog = true
                                            }
                                        },
                                        onFailure = {
                                            showErrorDialog = true
                                        }
                                    )

                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = softGreen,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "Guardar")
                            }
                        }
                    }

                    if (showSuccessDialog) {
                        AlertDialog(
                            onDismissRequest = { showSuccessDialog = false },
                            title = { Text("Éxito") },
                            text = { Text("Los productos se marcaron como entregados correctamente.") },
                            confirmButton = {
                                Button(onClick = { showSuccessDialog = false }) {
                                    Text("OK")
                                }
                            }
                        )
                    }

                    if (showErrorDialog) {
                        AlertDialog(
                            onDismissRequest = { showErrorDialog = false },
                            title = { Text("Error") },
                            text = { Text("Hubo un error al actualizar algunos productos.") },
                            confirmButton = {
                                Button(onClick = { showErrorDialog = false }) {
                                    Text("Reintentar")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}