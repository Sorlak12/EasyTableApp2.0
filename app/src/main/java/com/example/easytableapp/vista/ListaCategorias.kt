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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.text.style.TextOverflow

import com.example.easytableapp.modelo.Categoria
import com.example.easytableapp.modelo.Comensal
import com.example.easytableapp.controlador.ApiController
import com.example.easytableapp.modelo.ExtraData
import com.example.easytableapp.modelo.Producto
import com.example.easytableapp.ui.purple
import com.example.easytableapp.ui.softGreen
import com.example.easytableapp.ui.softRed

@Composable
fun ListaCategorias(navController: NavController, idMesa: Int, idComensal: Int) {
    // Flag para saber si se ha cargado la lista
    val isLoading = remember { mutableStateOf(true) }
    // Llamada a la API para obtener las categorias
    val listaCategorias = remember { mutableStateListOf<Categoria>() }
    val listaProductos = remember { mutableStateListOf<Producto>() }
    var searchQuery by remember { mutableStateOf("") } // Estado para almacenar la búsqueda
    var comensalLocal by remember { mutableStateOf<Comensal?>(null) }
    // Dialogo para cambiar PDV
    val showPDVDialog = remember { mutableStateOf(false) }
    val selectedPDV = remember { mutableStateOf("Cambiar carta") } // Estado para almacenar el PDV seleccionado
    val productoSeleccionado = remember { mutableStateOf<Producto?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val inputUsuario = remember { mutableStateOf("") }
    val cantidadSeleccionada = remember { mutableIntStateOf(1) }
    LaunchedEffect(Unit) {
        // Solicitud para obtener categorias
        ApiController.obtenerCategorias( idMesa,
            { categorias ->
                listaCategorias.clear()
                listaCategorias.addAll(categorias)
                isLoading.value = false
            },
            {
                Log.e("APIGCA", "Error: ${it.localizedMessage}")
                isLoading.value = false
            }
        )
        ApiController.obtenerTodosLosProductos(
            onSuccess = { productos ->
                listaProductos.clear()
                listaProductos.addAll(productos)
            },
            onFailure = {
                Log.e("APIProductos", "Error: ${it.localizedMessage}")
            }
        )


        // Solicitud para obtener comensal
        ApiController.obtenerDatosComensal(idComensal,
            {
                comensalLocal = it
                isLoading.value = false
            },
            {
                Log.e("API", "Error: ${it.localizedMessage}")
                isLoading.value = false
            })
    }

    LaunchedEffect(showDialog.value) {
        if (showDialog.value) inputUsuario.value = ""
    }


    // Diálogo ORIGINAL de confirmación para agregar producto
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Confirmación") },
            text = {
                Column {
                    Text("¿Está seguro de que desea agregar ${productoSeleccionado.value?.NombreProducto} al pedido?")
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
                        val extras: List<ExtraData> = emptyList()

                        ApiController.agregarProducto(
                            idComensal,
                            productoSeleccionado.value?.IDProducto ?: 0,
                            cantidadSeleccionada.intValue,
                            0,
                            notaFinal,
                            extras,
                            onSuccess = {
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
            Button (
                onClick = {
                    showPDVDialog.value = true
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
                Text(selectedPDV.value)
            }
            if (showPDVDialog.value) {
                AlertDialog (
                    onDismissRequest = { showPDVDialog.value = false },
                    title = { Text("Cambiar carta") },
                    text = {
                        Column (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button (
                                modifier = Modifier.width(150.dp),
                                onClick = {
                                    // Cambiar lista de categorias por la de otro PDV
                                    ApiController.obtenerCategoriasPDV(1,
                                        { categorias ->
                                            listaCategorias.clear()
                                            listaCategorias.addAll(categorias)
                                            isLoading.value = false
                                        },
                                        {
                                            Log.e("APIGCA", "Error: ${it.localizedMessage}")
                                            isLoading.value = false
                                        }
                                    )
                                    selectedPDV.value = "Carta: Restaurant"
                                    showPDVDialog.value = false
                                },
                                colors = ButtonDefaults.buttonColors (
                                    containerColor = purple,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Restaurant")
                            }
                            Button (
                                modifier = Modifier.width(150.dp),
                                onClick = {
                                    // Cambiar lista de categorias por la de otro PDV
                                    ApiController.obtenerCategoriasPDV(2,
                                        { categorias ->
                                            listaCategorias.clear()
                                            listaCategorias.addAll(categorias)
                                            isLoading.value = false
                                        },
                                        {
                                            Log.e("APIGCA", "Error: ${it.localizedMessage}")
                                            isLoading.value = false
                                        }
                                    )
                                    selectedPDV.value = "Carta: Bar"
                                    showPDVDialog.value = false
                                },
                                colors = ButtonDefaults.buttonColors (
                                    containerColor = purple,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Bar")
                            }
                            Button (
                                modifier = Modifier.width(150.dp),
                                onClick = {
                                    // Cambiar lista de categorias por la de otro PDV
                                    ApiController.obtenerCategoriasPDV(3,
                                        { categorias ->
                                            listaCategorias.clear()
                                            listaCategorias.addAll(categorias)
                                            isLoading.value = false
                                        },
                                        {
                                            Log.e("APIGCA", "Error: ${it.localizedMessage}")
                                            isLoading.value = false
                                        }
                                    )
                                    selectedPDV.value = "Carta: Cafetería"
                                    showPDVDialog.value = false
                                },
                                colors = ButtonDefaults.buttonColors (
                                    containerColor = purple,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cafetería")
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        Button (
                            onClick = { showPDVDialog.value = false },
                            colors = ButtonDefaults.buttonColors (
                                containerColor = Color.LightGray,
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
                    text = "Mesa ${idMesa}: ${comensalLocal?.NombreComensal ?: ""} ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            // Contenedor de la barra de busqueda
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
            ) {
                val keyboardController = LocalSoftwareKeyboardController.current
                // Campo de búsqueda
                TextField (
                    value = searchQuery,
                    onValueChange = { newValue -> searchQuery = newValue },
                    label = { Text("Ingrese nombre de categoría", fontWeight = FontWeight.Normal) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                )
            }

            // Contenedor de categorias
            Column {
                if (isLoading.value) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Cargando categorías...",
                                fontSize = 20.sp,
                                color = Color.LightGray,
                            )
                        }
                    }
                } else {
                    val filteredProductos = listaProductos.filter {
                        it.NombreProducto.contains(searchQuery, ignoreCase = true)
                    }

                    if (searchQuery.isNotBlank() && filteredProductos.isNotEmpty()) {
                        // Mostrar productos que coincidan con la búsqueda
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(filteredProductos.chunked(2)) { productos ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    productos.forEach { producto ->
                                        Button(
                                            onClick = {
                                                if (producto.IDCategoria == 25 || producto.IDCategoria == 28) {
                                                    navController.navigate("ver_extras/${producto.IDProducto}/${idMesa}/${idComensal}")
                                                } else {
                                                    ApiController.obtenerProductoPorId(producto.IDProducto, {
                                                        productoSeleccionado.value = it
                                                        showDialog.value = true
                                                    }, {
                                                        Log.e("APILP", "Error: ${it.localizedMessage}")
                                                    })
                                                }
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(80.dp)
                                                .padding(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.LightGray,
                                                contentColor = Color.Black
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = producto.NombreProducto,
                                                fontSize = 16.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        val filteredCategorias = listaCategorias.filter {
                            it.NombreCategoria.contains(searchQuery, ignoreCase = true)
                        }

                        if (filteredCategorias.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "No se encontraron categorías",
                                        fontSize = 20.sp,
                                        color = Color.LightGray,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                        } else {
                            // Mostrar categorías normalmente
                            LazyColumn(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(filteredCategorias.chunked(2)) { rowItems ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        rowItems.forEach { categoria ->
                                            Button(
                                                onClick = {
                                                    navController.navigate("ver_productos/${categoria.IDCategoria}/$idMesa/${comensalLocal?.IDComensal}")
                                                },
                                                modifier = Modifier
                                                    .size(90.dp)
                                                    .weight(1f)
                                                    .padding(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color.LightGray,
                                                    contentColor = Color.Black
                                                ),
                                                shape = RoundedCornerShape(8.dp),
                                            ) {
                                                Text(categoria.NombreCategoria)
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