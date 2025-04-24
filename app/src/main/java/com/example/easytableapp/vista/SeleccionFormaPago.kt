package com.example.easytableapp.vista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SeleccionFormaPago(navController: NavController, mesaId: Int){
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
                IconButton(onClick = { navController.navigate("detalles_mesa/${mesaId}") }) {
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
                    text = "Pago: Mesa $mesaId",
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                Text (
                    text = "Seleccione forma de pago",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
            }
            // Contenedor de las opciones
            Column (
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button (
                    onClick = {
                        navController.navigate("pago/$mesaId/0")
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("👤 Total", fontSize = 18.sp)
                }
                Button (
                    onClick = {
                        navController.navigate("pago/$mesaId/1")
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("👥 Por partes", fontSize = 18.sp)
                }
            }
        }
    }
}