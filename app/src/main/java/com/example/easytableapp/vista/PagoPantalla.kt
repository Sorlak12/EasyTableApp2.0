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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.easytableapp.controlador.ApiController
import com.example.easytableapp.modelo.Comensal
import com.example.easytableapp.modelo.Mesa
import com.example.easytableapp.ui.softGreen
import com.example.easytableapp.ui.softRed
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PagoPantalla(idMesa: Int, flag: Int, navController: NavController) {
    // Flag para saber si se ha cargado la lista
    val isLoading = remember { mutableStateOf(true) }
    // Llamada a la API para obtener los comensales
    val comensales = remember { mutableStateOf(emptyList<Comensal>()) }
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val mesaLocal = remember { mutableStateOf<Mesa?>(null) }
    val totalMesa = remember { mutableIntStateOf(0) }
    LaunchedEffect(idMesa) {
        ApiController.obtenerComensalesPorMesa(idMesa,
            {
                comensales.value = it
                isLoading.value = false
            },
            {
                Log.e("API", "Error: ${it.localizedMessage}")
                isLoading.value = false
            })
        ApiController.obtenerMesaPorId(idMesa, {
            mesaLocal.value = it
            isLoading.value = false
        }, { })
        ApiController.obtenerTotalMesa(idMesa, {
            totalMesa.intValue = it
            isLoading.value = false
        }, { })
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
                IconButton(onClick = { navController.navigate("forma_pago/${idMesa}") }) {
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
            // Contenedor de titulo de la pantalla
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text (
                    text = "Mesa $idMesa",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                Text (
                    text = "Marcar comensales como pagados",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }

            // Contenedor de comensales
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
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
                            Spacer(modifier = Modifier.height(16.dp))
                            Text (
                                text = "Cargando comensales...",
                                fontSize = 20.sp,
                                color = Color.LightGray,
                            )
                        }
                    }
                } else if (comensales.value.isEmpty()) {
                    // Mostrar mensaje de error si no hay datos
                    Box (
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No se encontraron comensales",
                                fontSize = 20.sp,
                                color = Color.LightGray,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                } else if (comensales.value.all { comensal -> comensal.Pagado == 1 }) {
                    // Mostrar mensaje si todos los comensales han pagado
                    Box (
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text (
                                text = "Todos los comensales han pagado",
                                fontSize = 20.sp,
                                color = Color.LightGray,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    val showLimpiarDialog = remember { mutableStateOf(false) }
                    // Mostrar el popup de confirmación
                    if (showLimpiarDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showLimpiarDialog.value = false },
                            title = { Text("Confirmación") },
                            text = { Text("¿Está seguro de que desea limpiar esta mesa?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        ApiController.marcarMesaComoPagada(idMesa, {
                                            navController.navigate("seleccionar_mesa/${mesaLocal.value?.IDPDV}")
                                        }, {
                                            Log.e("API", "Error: ${it.localizedMessage}")
                                        })
                                    },
                                    modifier = Modifier.weight(1f),
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
                                    onClick = { showLimpiarDialog.value = false },
                                    modifier = Modifier
                                        .weight(1f),
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

                    // Botón para limpiar la mesa
                    Button (
                        onClick = { showLimpiarDialog.value = true },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
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
                                contentDescription = "Limpiar mesa",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Limpiar mesa", fontSize = 18.sp)
                        }
                    }
                } else {
                    // Pago entero
                    if (flag == 0) {
                        LazyColumn (
                            modifier = Modifier.weight(1f) // Permite que se ajuste correctamente
                        ) {
                            items(comensales.value) { comensal ->
                                if (comensal.Pagado != 1) {
                                    val totalComensal = remember { mutableIntStateOf(0) }
                                    ApiController.obtenerTotalComensal(comensal.IDComensal, { totalComensal.intValue = it },
                                        { Log.e("API", "Error: ${it.localizedMessage}") })

                                    Column (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row (
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text (
                                                text = comensal.NombreComensal,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Normal,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text (
                                                text = "Total: $${numberFormat.format(totalComensal.intValue)}",
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        val showDialog = remember { mutableStateOf(false) }
                        // Mostrar el popup de confirmación
                        if (showDialog.value) {
                            AlertDialog(
                                onDismissRequest = { showDialog.value = false },
                                title = { Text("Confirmación") },
                                text = { Text("¿Está seguro de que desea marcar esta mesa como pagada?") },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            ApiController.marcarMesaComoPagada(idMesa, {
                                                navController.navigate("seleccionar_mesa/${mesaLocal.value?.IDPDV}")
                                            }, {
                                                Log.e("API", "Error: ${it.localizedMessage}")
                                            })
                                        },
                                        modifier = Modifier.weight(1f),
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
                                        modifier = Modifier
                                            .weight(1f),
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

                        // Informacion de pago
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text (
                                text = "A pagar: $${numberFormat.format(totalMesa.intValue)}",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(16.dp),
                            )
                            Button (
                                onClick = { showDialog.value = true },
                                modifier = Modifier
                                    .height(50.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = softGreen,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon (
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Pagar",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text("Pagar", fontSize = 18.sp)
                                }
                            }

                        }
                    } else if (flag == 1) {
                        // Pago por partes
                        LazyColumn (
                            modifier = Modifier.weight(1f) // Permite que se ajuste correctamente
                        ) {
                            items(comensales.value) { comensal ->
                                // Estado para mostrar el popup
                                val showDialog = remember { mutableStateOf(false) }
                                Row (
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (comensal.Pagado != 1) {
                                        val totalComensal = remember { mutableIntStateOf(0) }
                                        ApiController.obtenerTotalComensal(comensal.IDComensal, { totalComensal.intValue = it },
                                            { Log.e("API", "Error: ${it.localizedMessage}") })

                                        Text (
                                            text = comensal.NombreComensal,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Normal,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text (
                                            text = "Total: $${numberFormat.format(totalComensal.intValue)}",
                                            fontSize = 18.sp
                                        )
                                        Button (
                                            onClick = { showDialog.value = true },
                                            colors = ButtonDefaults.buttonColors (
                                                containerColor = softGreen,
                                                contentColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text(text = "Pagar")
                                        }
                                    }
                                }
                                // Mostrar el popup de confirmación
                                if (showDialog.value) {
                                    AlertDialog(
                                        onDismissRequest = { showDialog.value = false },
                                        title = { Text("Confirmación") },
                                        text = { Text("¿Está seguro de que desea marcar a ${comensal.NombreComensal} como pagado?") },
                                        confirmButton = {
                                            Button(
                                                onClick = {
                                                    ApiController.marcarComensalComoPagado(comensal.IDComensal,
                                                        {
                                                            // Actualizar la lista de comensales
                                                            ApiController.obtenerComensalesPorMesa(idMesa,
                                                                { comensales.value = it },
                                                                { Log.e("API", "Error: ${it.localizedMessage}") })
                                                            showDialog.value = false
                                                        },
                                                        { Log.e("API", "Error: ${it.localizedMessage}") })
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
                            }
                        }
                    }
                }
            }
        }
    }
}