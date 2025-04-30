package com.example.easytableapp.vista

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.easytableapp.controlador.RetrofitClient
import com.example.easytableapp.modelo.Mesa
import com.example.easytableapp.ui.celesteFornite

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun SeleccionMesaPantalla(navController: NavController, idPdv: Int){
    // Flag para saber si se ha cargado la lista
    val isLoading = remember { mutableStateOf(true) }
    // Llamada a la API para obtener las mesas del PDV y las mesas
    val mesas = remember { mutableStateOf(emptyList<Mesa>()) }
    LaunchedEffect(Unit) {
        val call = RetrofitClient.api.getMesasPorPDV(idPdv)
        call.enqueue(object : Callback<List<Mesa>> {
            override fun onResponse(call: Call<List<Mesa>>, response: Response<List<Mesa>>) {
                if (response.isSuccessful) {
                    mesas.value = response.body() ?: emptyList()
                    isLoading.value = false
                } else {
                    Log.e("API", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Mesa>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
            }
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
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                IconButton(onClick = { navController.navigate("seleccionar_punto") }) {
                    Icon (
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        }

        // Separador del navbar
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

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
                        text = "Cargando datos...",
                        fontSize = 20.sp,
                        color = Color.LightGray,
                    )
                }
            }
        } else if (mesas.value.isEmpty()) {
            // Mostrar mensaje si no se encuentran datos
            Box (
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text (
                    text = "No se encontraron mesas",
                    fontSize = 20.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Normal
                )
            }
        } else {
            // Botones de mesas
            LazyColumn (
                modifier = Modifier
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(mesas.value.chunked(3)) { rowItems ->
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowItems.forEach { mesa ->
                            Button (
                                onClick = { navController.navigate("detalles_mesa/${mesa.IDMesa}") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (mesa.TieneComensales == 1) celesteFornite else Color.LightGray,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "Mesa ${mesa.IDMesa}", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}