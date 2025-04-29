package com.example.easytableapp.vista

import android.util.Log
import androidx.compose.foundation.border
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
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue

import com.example.easytableapp.controlador.ApiController
import com.example.easytableapp.modelo.Comensal
import com.example.easytableapp.modelo.Mesa
import com.example.easytableapp.ui.gold
import com.example.easytableapp.ui.purple
import com.example.easytableapp.ui.softGreen
import com.example.easytableapp.ui.softRed

import java.text.NumberFormat
import java.util.Locale

@Composable
fun DetallesMesaPantalla(navController: NavController, idMesa: Int){
    // Flag para saber si se ha cargado la lista
    val isLoading = remember { mutableStateOf(true) }
    // Variable para formatear numeros en formato de moneda
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    // Variable para almacenar la lista de comensales
    val comensales = remember { mutableStateOf(emptyList<Comensal>()) }
    // Variable para almacenar la mesa
    val mesa = remember { mutableStateOf<Mesa?>(null) }
    // Variable para almacenar el total de la mesa
    val totalMesa = remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        // Solicitud para obtener comensales
        ApiController.obtenerComensalesPorMesa(idMesa,
            {
                comensales.value = it
                isLoading.value = false
            },
            {
                Log.e("API", "Error: ${it.localizedMessage}")
                isLoading.value = false
            })

        // Solicitud para obtener el total de la mesa
        ApiController.obtenerTotalMesa(idMesa,
            {
                totalMesa.intValue = it
                isLoading.value = false
            },
            {
                Log.e("API", "Error: ${it.localizedMessage}")
                isLoading.value = false
            })
        // Solicitud para obtener la mesa
        ApiController.obtenerMesaPorId(idMesa, { mesa.value = it }, { })
    }

    // Ventana de dialogo para añadir comensales
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        var nombreComensal by remember { mutableStateOf(TextFieldValue("")) }
        AlertDialog (
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Ingrese el nombre del comensal", fontSize = 20.sp) },
            text = {
                Column {
                    // Prevenir la accion default del boton enter en el teclado
                    val keyboardController = LocalSoftwareKeyboardController.current
                    OutlinedTextField (
                        value = nombreComensal,
                        onValueChange = { nombreComensal = it },
                        label = { Text("Nombre del comensal", fontWeight = FontWeight.Normal) },
                        singleLine = true,
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button (
                    onClick = {
                        if (nombreComensal.text.isNotBlank()) {
                            // Llamada API para agregar comensal
                            ApiController.agregarComensal( nombreComensal.text, idMesa,
                                {
                                    // Actualizar la lista de comensales
                                    ApiController.obtenerComensalesPorMesa(idMesa,
                                        {
                                            comensales.value = it

                                            showDialog.value = false
                                        },
                                        { Log.e("API", "Error: ${it.localizedMessage}")})
                                },
                                { Log.e("ApiControllerAgregarComensal", "Error al agregar comensal")})
                        }
                    },
                    colors = ButtonDefaults.buttonColors (
                        containerColor = softGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                Button (
                    onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Volver")
                }
            }
        )
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
                IconButton(onClick = { navController.navigate("seleccionar_mesa/${mesa.value?.IDPDV}") }) {
                    Icon (
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
            // Botón para ver resumen de la mesa
            if (totalMesa.intValue > 0) {
                Button (
                    onClick = {
                        navController.navigate("resumen_mesa/${idMesa}")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = purple,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Ver resumen")
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
                    text = "Mesa ${mesa.value?.IDMesa}",
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
                } else if (comensales.value.isEmpty()) {
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
                        items(comensales.value) { comensal ->
                            // Solicitud para obtener el total de pedidos de un comensal
                            val totalComensal = remember { mutableIntStateOf(0) }
                            ApiController.obtenerTotalComensal(comensal.IDComensal, { totalComensal.intValue = it },
                                { Log.e("API", "Error: ${it.localizedMessage}") })
                            Column (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                                    .padding(8.dp),
                            ) {
                                if (comensal.Pagado == 1) {
                                    Row (
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text (
                                            text = comensal.NombreComensal,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Normal,
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .weight(1f)
                                        )
                                        Text (
                                            text = "Total: $${numberFormat.format(totalComensal.intValue)}",
                                            fontSize = 18.sp
                                        )
                                        Button (
                                            onClick = { navController.navigate("resumen_comensal/${comensal.IDComensal}") },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = gold,
                                                contentColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .height(40.dp)
                                                .align(Alignment.CenterVertically)
                                                .padding(start = 8.dp)
                                        ) {
                                            Text (
                                                text = "Pagado",
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
                                } else {
                                    Row (
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text (
                                            text = comensal.NombreComensal,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Normal,
                                            modifier = Modifier
                                                .align(Alignment.CenterVertically)
                                                .weight(1f)
                                        )
                                        Row (
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text (
                                                text = "Total:  $${numberFormat.format(totalComensal.intValue)}",
                                                fontSize = 18.sp
                                            )
                                            IconButton(onClick = { navController.navigate("resumen_comensal/${comensal.IDComensal}") }) {
                                                Icon (
                                                    imageVector = Icons.Filled.Info,
                                                    contentDescription = "Ver pedido"
                                                )
                                            }
                                        }
                                    }
                                    Row (
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        // Botón para agregar productos
                                        Button (
                                            onClick = { navController.navigate("agregar_productos/${comensal.IDComensal}/${idMesa}")},
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp),
                                            colors = ButtonDefaults.buttonColors (
                                                containerColor = softGreen,
                                                contentColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text (
                                                text = "Agregar producto(s)",
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        // Botón para eliminar productos
                                        Button (
                                            onClick = { navController.navigate("eliminar_productos/${comensal.IDComensal}")},
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp),
                                            colors = ButtonDefaults.buttonColors (
                                                containerColor = softRed,
                                                contentColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text (
                                                text = "Eliminar producto(s)",
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
            ) {
                Row {
                    // Botón para añadir comensales
                    Button (
                        onClick = { showDialog.value = true },
                        modifier = Modifier
                            .padding(4.dp)
                            .height(50.dp)
                            .weight(1f),
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
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Añadir comensal"
                            )
                            Text("+", fontSize = 18.sp)
                        }
                    }
                    // Botón para eliminar comensales
                    Button (
                        onClick = {
                            navController.navigate("eliminar_comensal/$idMesa")
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .height(50.dp)
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors (
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
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Eliminar comensal"
                            )
                            Text("-", fontSize = 18.sp)
                        }
                    }
                }
                Row {
                    // Botón para pagar el pedido
                    Button (
                        onClick = {
                            navController.navigate("forma_pago/$idMesa")
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .height(50.dp)
                            .weight(1f),
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
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Pagar pedido",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Pagar pedido", fontSize = 18.sp)
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
                                            navController.navigate("seleccionar_mesa/${mesa.value?.IDPDV}")
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

                    // Botón para limpiar la mesa si todos los comensales estan pagados
                    if (comensales.value.isNotEmpty() && comensales.value.all { comensal -> comensal.Pagado == 1 }) {
                        // Botón para limpiar la mesa
                        Button (
                            onClick = { showLimpiarDialog.value = true },
                            modifier = Modifier
                                .padding(4.dp)
                                .height(50.dp)
                                .weight(1f),
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
                                    contentDescription = "Pagar",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Limpiar mesa", fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}