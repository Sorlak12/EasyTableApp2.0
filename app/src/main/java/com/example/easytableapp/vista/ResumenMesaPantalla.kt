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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.easytableapp.controlador.ApiController
import com.example.easytableapp.modelo.Comensal
import com.example.easytableapp.modelo.Comensal_Producto
import com.example.easytableapp.modelo.Comensal_Producto_Extra
import com.example.easytableapp.modelo.Extra
import com.example.easytableapp.modelo.Producto
import com.example.easytableapp.ui.purple
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResumenMesaPantalla(navController: NavController, idMesa: Int) {
    // Flag para saber si se ha cargado la lista
    val isLoading = remember { mutableStateOf(true) }
    // Variable para cambiar el formato de los valores numericos
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    // Variables para almacenar la informacion de la mesa
    var comensales by remember { mutableStateOf(emptyList<Comensal?>()) }
    var productos by remember { mutableStateOf(emptyList<Producto>()) }
    var extras by remember { mutableStateOf(emptyList<Extra>()) }
    var comensalProducto by remember { mutableStateOf(emptyList<Comensal_Producto>()) }
    var comensalProductoExtra by remember { mutableStateOf(emptyList<Comensal_Producto_Extra>()) }
    // Variable para almacenar el total de la mesa
    val totalMesa = remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        // Solicitud para obtener el comensal
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

        ApiController.obtenerTotalMesa(idMesa,
            {
                totalMesa.intValue = it
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
                IconButton(onClick = { navController.navigate("detalles_mesa/${idMesa}") }) {
                    Icon (
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
            // Botón para ver entregados
            Button (
                onClick = {
                    navController.navigate("entregado_pantalla/${idMesa}")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 8.dp)
            )

            {
                Text(text = "Marcar entregados")
            }
        }

        // Separador del navbar
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        // Contenedor de contenido
        Column (
            modifier = Modifier
                .padding(32.dp)
        ) {
            // Contenedor de titulo y total de la mesa
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Titulo de la mesa
                Text (
                    text = "Mesa $idMesa",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Total de la mesa
                Text (
                    text = "Total: $${numberFormat.format(totalMesa.intValue)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
            }
            // Contenedor de comensales
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                // Mostrar pantalla de carga mientras se obtienen los datos
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
                                text = "Cargando comensales...",
                                fontSize = 20.sp,
                                color = Color.LightGray,
                            )
                        }
                    }
                } else if (comensales.isEmpty()) {
                    // Mostrar mensaje de error si no hay datos
                    Box (
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text (
                            text = "No se encontraron comensales",
                            fontSize = 20.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Normal
                        )
                    }
                } else {
                    LazyColumn (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Recorremos todos los comensales
                        items(comensales) { comensal ->
                            if (comensal != null) {
                                // Recorremos los productos del comensal
                                val comensalProductos = comensalProducto.filter { it.IDComensal == comensal.IDComensal }
                                Column (
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                ) {
                                    // Título para el comensal
                                    Text (
                                        text = comensal.NombreComensal,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                    // Recorremos los productos de este comensal
                                    comensalProductos.forEach { pedido ->
                                        val producto = productos.find { it.IDProducto == pedido.IDProducto } // Buscar el producto
                                        val extrasDelProducto = comensalProductoExtra.filter { it.IDProducto == pedido.IDProducto && it.IDComensal == pedido.IDComensal } // Buscar los extras del producto

                                        // Mostrar el producto y su cantidad
                                        Column (
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)
                                        ) {
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
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text (
                                                    text = "Subtotal: $${numberFormat.format(pedido.cantidad * (producto?.ValorProducto ?: 0))}"
                                                )
                                            }

                                            // Mostrar los extras de este producto
                                            extrasDelProducto.forEach { extraPedido ->
                                                val extra = extras.find { it.IDExtra == extraPedido.IDExtra }
                                                extra?.takeIf { extraPedido.cantidad >= 1 }?.let {
                                                    Row (
                                                        modifier = Modifier
                                                            .padding(start = 16.dp)
                                                            .fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text (
                                                            text = "➜ ${extra.NombreExtra} (x${extraPedido.cantidad})",
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Normal
                                                        )
                                                        Text (
                                                            text = "+$${numberFormat.format(extraPedido.cantidad * extra.ValorExtra)}",
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Normal
                                                        )
                                                    }
                                                }
                                            }

                                            // Mostrar las notas del pedido
                                            pedido.Notas?.takeIf { it.isNotBlank() }?.let { nota ->
                                                Text(
                                                    text = "📝 Nota: $nota",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    color = Color.Blue,
                                                    modifier = Modifier
                                                        .padding(top = 4.dp, start = 16.dp)
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
        }
    }
}