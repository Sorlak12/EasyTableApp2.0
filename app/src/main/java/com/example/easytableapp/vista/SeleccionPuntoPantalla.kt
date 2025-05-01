package com.example.easytableapp.vista

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.easytableapp.R
import com.example.easytableapp.controlador.RetrofitClient
import com.example.easytableapp.modelo.Pdv

// Librerias Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SeleccionPuntoPantalla(navController: NavController) {
    // Flag para saber si se ha cargado la lista
    val isLoading = remember { mutableStateOf(true) }

    // Llamada a la API para obtener los PDVs
    val sectores = remember { mutableStateOf(emptyList<Pdv>()) }

    LaunchedEffect(Unit) {
        val call = RetrofitClient.api.getPDV()
        call.enqueue(object : Callback<List<Pdv>> {
            override fun onResponse(call: Call<List<Pdv>>, response: Response<List<Pdv>>) {
                if (response.isSuccessful) {
                    sectores.value = response.body() ?: emptyList()
                    isLoading.value = false
                } else {
                    Log.e("API", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Pdv>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
            }
        })
    }

    // Mostrar pantalla de carga mientras se obtienen los datos
    if (isLoading.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
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
    } else if (sectores.value.isEmpty()) {
        // Mostrar mensaje de error si no hay datos
        Box (
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text (
                text = "No se encontraron PDVs",
                fontSize = 20.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Normal
            )
        }
    } else {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column (
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Row (
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text (
                        text = "EasyTable",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Image (
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo de la aplicación",
                        modifier = Modifier.height(150.dp)
                    )
                }
                // Operaciones
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Texto de guia
                    Text (
                        text = "Seleccione PDV",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                    )
                    // Botones de PDVs
                    sectores.value.forEach { sector ->
                        Button (
                            onClick = { navController.navigate("seleccionar_mesa/${sector.IDPDV}") },
                            modifier = Modifier
                                .padding(10.dp)
                                .width(220.dp)
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = sector.NombrePDV,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
            // Footer de version
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "Versión 2.1",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}