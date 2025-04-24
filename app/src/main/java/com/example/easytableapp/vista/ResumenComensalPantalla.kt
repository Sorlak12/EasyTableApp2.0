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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.easytableapp.controlador.ApiController
import com.example.easytableapp.modelo.Comensal
import com.example.easytableapp.modelo.Comensal_Producto
import com.example.easytableapp.modelo.Comensal_Producto_Extra
import com.example.easytableapp.modelo.Extra
import com.example.easytableapp.modelo.Producto
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResumenComensalPantalla(navController: NavController, idComensal: Int) {
    // Flag para saber si se ha cargado la lista
    val isLoading = remember { mutableStateOf(true) }
    // Variable para formatear numeros en formato de moneda
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    //  Variable para almacenar la informacion del comensal
    var comensal by remember { mutableStateOf<Comensal?>(null) }
    // Variables para almacenar los productos del comensal
    var productos by remember { mutableStateOf(emptyList<Producto>()) }
    var extras by remember { mutableStateOf(emptyList<Extra>()) }
    var comensalProducto by remember { mutableStateOf(emptyList<Comensal_Producto>()) }
    var comensalProductoExtra by remember { mutableStateOf(emptyList<Comensal_Producto_Extra>()) }
    // Variable para almacenar el pago del comensal
    val totalComensal = remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        // Solicitud para obtener el comensal
        ApiController.obtenerDatosComensal(idComensal,
            {
                comensal = it
                isLoading.value = false
            },
            {
                Log.e("API", "Error: ${it.localizedMessage}")
                isLoading.value = false
            })

        // Solicitud para obtener los productos del comensal
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

        // Solicitud para obtener el total del comensal
        ApiController.obtenerTotalComensal(idComensal,
            {
                totalComensal.intValue = it
                isLoading.value = false
            },
            {
                Log.e("API", "Error: ${it.localizedMessage}")
                isLoading.value = false
            })
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
                IconButton(onClick = { navController.navigate("detalles_mesa/${comensal?.IDMesa}") }) {
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
            // Contenedor de informacion del comensal
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text (
                    text = "Mesa ${comensal?.IDMesa}: ${comensal?.NombreComensal}",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                Text (
                    text = "Total: $${numberFormat.format(totalComensal.intValue)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            // Contenedor de productos del comensal
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Mostrar pantalla de carga mientras se obtienen los datos
                if (isLoading.value) {
                    Box (
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text (
                                text = "Cargando productos...",
                                fontSize = 20.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                } else if (comensalProducto.isEmpty()) {
                    // Mostrar mensaje de error si no hay datos
                    Box (
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No hay productos",
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
                            val extrasPedido = comensalProductoExtra.filter { it.IDComensal == pedido.IDComensal && it.IDProducto == pedido.IDProducto }
                            // Contenedor de cada producto
                            Column (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                // Nombre del producto y cantidad
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text (
                                        text = "${producto?.NombreProducto ?: "Desconocido"} (x${pedido.cantidad})",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                    Text (
                                        text = "Subtotal: $${numberFormat.format(pedido.cantidad * (producto?.ValorProducto ?: 0))}",
                                        fontSize = 18.sp,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                    )
                                }

                                // Extras
                                extrasPedido.forEach { extraPedido ->
                                    val extra = extras.find { it.IDExtra == extraPedido.IDExtra }
                                    extra?.takeIf { extraPedido.cantidad >= 1 }?.let {
                                        Row (
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
                                    Text (
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