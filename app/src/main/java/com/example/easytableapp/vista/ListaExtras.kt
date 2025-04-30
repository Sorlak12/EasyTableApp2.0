package com.example.easytableapp.vista

import android.os.Debug
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.easytableapp.controlador.ApiController
import com.example.easytableapp.modelo.Extra
import com.example.easytableapp.modelo.Producto
import com.example.easytableapp.modelo.ExtraData
import com.example.easytableapp.ui.softGreen
import com.example.easytableapp.ui.softRed
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaExtras(idProducto: Int, idMesa: Int, idComensal: Int, navController: NavController) {
    val isLoading = remember { mutableStateOf(true) }
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val listaExtras = remember { mutableStateListOf<Extra>() }
    val extrasSeleccionados = remember { mutableStateMapOf<Int,Int>() }

    LaunchedEffect(Unit) {
        ApiController.obtenerExtras({ extras ->
            listaExtras.clear()
            listaExtras.addAll(extras)
            isLoading.value = false
        }, {
            Log.e("GetExtras", "Error: $it")
            isLoading.value = false
        })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Selecciona los extras") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        if (isLoading.value) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando extras...", fontSize = 20.sp, color = Color.LightGray)
                }
            }
        } else if (listaExtras.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se encontraron extras", fontSize = 20.sp, color = Color.LightGray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(listaExtras.size) { index ->
                    val extra = listaExtras[index]
                    var cantidad by remember { mutableIntStateOf(0) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = extra.NombreExtra ?: "",
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "+$${numberFormat.format(extra.ValorExtra * cantidad)}",
                            modifier = Modifier.padding(end = 8.dp),
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Button(
                                onClick = {
                                    if (cantidad > 0) cantidad -= 1
                                    if (cantidad > 0) {
                                        extrasSeleccionados[extra.IDExtra] = cantidad
                                    } else {
                                        extrasSeleccionados.remove(extra.IDExtra)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("-")
                            }
                            Text(cantidad.toString())
                            Button(
                                onClick = {
                                    cantidad += 1
                                    extrasSeleccionados[extra.IDExtra] = cantidad
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+")
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                val extrasArray: List<ExtraData> = extrasSeleccionados
                    .filter { it.value > 0 } // Filtrar solo los extras con cantidad > 0
                    .map { (idExtra, cantidad) ->
                        ExtraData(idExtra, cantidad)
                    }
                ProductoConAgregarButton(
                    idComensal = idComensal,
                    idProducto = idProducto,
                    extras = extrasArray,
                    idMesa = idMesa,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun ProductoConAgregarButton(idComensal: Int, idProducto: Int, extras: List<ExtraData>, idMesa: Int, navController: NavController) {
    val showDialog = remember { mutableStateOf(false) }
    val inputUsuario = remember { mutableStateOf("") }
    val cantidadSeleccionada = remember { mutableIntStateOf(1) }
    val producto = remember { mutableStateOf<Producto?>(null) }

    LaunchedEffect(Unit) {
        ApiController.obtenerProductoPorId(idProducto, {
            producto.value = it
        }, {
            Log.e("GetProducto", "Error: $it")
        })
    }

    LaunchedEffect(showDialog.value) {
        if (showDialog.value) inputUsuario.value = ""
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Confirmación") },
            text = {
                Column {
                    Text("¿Está seguro de que desea agregar ${producto.value?.NombreProducto} al pedido?")
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = inputUsuario.value,
                        onValueChange = { inputUsuario.value = it },
                        label = { Text("Comentario", fontWeight = FontWeight.Normal) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cantidad de Producto")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { if (cantidadSeleccionada.intValue > 1) cantidadSeleccionada.intValue -= 1 },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("-")
                        }
                        Text(cantidadSeleccionada.intValue.toString())
                        Button(
                            onClick = { cantidadSeleccionada.intValue += 1 },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val notaFinal = inputUsuario.value.ifBlank { " " }
                        Log.e("API", "Extras: $extras")

                        ApiController.agregarProducto(
                            idComensal,
                            idProducto,
                            cantidadSeleccionada.intValue,
                            0,
                            notaFinal,
                            extras,
                            onSuccess = { /*instanciaGenerada->
                                extras.forEach { extra ->
                                    if (extra.value > 0) {
                                        ApiController.agregarExtra(
                                            idComensal,
                                            idProducto,
                                            instanciaGenerada,
                                            extra.key,
                                            extra.value,
                                            onSuccess = { Log.d("API", "Extra agregado") },
                                            onFailure = { Log.e("API", "Error: ${it.localizedMessage}") }
                                        )
                                    }
                                }*/
                                showDialog.value = false
                                navController.navigate("detalles_mesa/$idMesa")
                            },
                            onFailure = { Log.e("API", "Error: ${it.localizedMessage}") }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = softGreen,contentColor = Color.Black),
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp)
    ) {
        Button(
            onClick = { showDialog.value = true },
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(containerColor = softGreen,contentColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Agregar", fontSize = 16.sp)
        }
    }
}