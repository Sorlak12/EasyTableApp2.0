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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight

import com.example.easytableapp.controlador.ApiController
import com.example.easytableapp.modelo.Comensal
import com.example.easytableapp.ui.softGreen
import com.example.easytableapp.ui.softRed


@Composable
fun EliminarComensalesPantalla(navController: NavController, idMesa: Int) {
    // Flag para saber si se ha cargado la lista
    val isLoading = remember { mutableStateOf(true) }
    // Llamada a la API para obtener los comensales de la mesa
    val comensales = remember { mutableStateOf(emptyList<Comensal>()) }
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
        }

        // Separador del navbar
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        // Contenedor de contenido
        Column (
            modifier = Modifier
                .padding(32.dp)
        ) {
            // Contenedor de titulo y subtitulo
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
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
                // Subtitulo de la mesa
                Text (
                    text = "Eliminar comensales",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }
            // Contenedor de comensales
            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
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
                } else {
                    LazyColumn (
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(comensales.value) { comensal ->
                            Column (
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
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
                                            .padding(bottom = 8.dp)
                                            .weight(1f)
                                    )
                                    // Estado para mostrar el popup
                                    val showDialog = remember { mutableStateOf(false) }

                                    // Mostrar el popup de confirmación
                                    if (showDialog.value) {
                                        AlertDialog(
                                            onDismissRequest = { showDialog.value = false },
                                            title = { Text("Confirmación") },
                                            text = { Text("¿Está seguro de que desea eliminar a ${comensal.NombreComensal} de la mesa?") },
                                            confirmButton = {
                                                Button(
                                                    onClick = {
                                                        // Logica para eliminar al comensal
                                                        ApiController.eliminarComensal(comensal.IDComensal,
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
                                                    colors = ButtonDefaults.buttonColors (
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

                                    // Botón para eliminar comensal
                                    Button (
                                        onClick = { showDialog.value = true },
                                        modifier = Modifier
                                            .height(50.dp)
                                            .padding(start = 8.dp),
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
                                                contentDescription = "Eliminar comensales"
                                            )
                                            Text("-", fontSize = 18.sp)
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