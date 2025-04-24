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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow

import com.example.easytableapp.controlador.ApiController
import com.example.easytableapp.modelo.Producto

@Composable
fun ListaProductos(navController: NavController, idCategoria: Int, idMesa: Int, idComensal: Int) {
    // Flag para saber si se ha cargado la lista
    val isLoading = remember { mutableStateOf(true) }
    // Llamada a la API para obtener los productos
    var searchQuery by remember { mutableStateOf("") } // Estado para almacenar la búsqueda
    val listaProductos = remember { mutableStateListOf<Producto>() }
    LaunchedEffect(Unit) {
        // Obtener los productos según la categoría
        ApiController.obtenerProductosPorCategoria(idCategoria, {
            listaProductos.clear()
            listaProductos.addAll(it)
            isLoading.value = false
        }, {
            Log.e("APILP", "Error: ${it.localizedMessage}")
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
                IconButton(onClick = { navController.navigate("agregar_productos/${idComensal}/${idMesa}") }) {
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
            // Contenedor de la barra de busqueda
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
            ) {
                // Campo de búsqueda
                val keyboardController = LocalSoftwareKeyboardController.current
                TextField (
                    value = searchQuery,
                    onValueChange = { newValue -> searchQuery = newValue },
                    label = { Text("Buscar producto", fontWeight = FontWeight.Normal) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
            // Contenedor de productos
            Column {
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
                                text = "Cargando productos...",
                                fontSize = 20.sp,
                                color = Color.LightGray,
                            )
                        }
                    }
                } else if (listaProductos.isEmpty()) {
                    // Mostrar mensaje de error si no hay datos
                    Box (
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (
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
                    // Filtrar productos según la búsqueda
                    val filteredProductos = listaProductos.filter {
                        it.NombreProducto.contains(searchQuery, ignoreCase = true)
                    }

                    // Lista de productos (solo muestra los que coinciden con la búsqueda)
                    LazyColumn (
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        filteredProductos.forEach { producto ->
                            item {
                                Button(
                                    onClick = { navController.navigate("ver_extras/${producto.IDProducto}/${idMesa}/${idComensal}") },
                                    modifier = Modifier
                                        .height(60.dp)
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.LightGray,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text (
                                        text = producto.NombreProducto,
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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