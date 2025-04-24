package com.example.easytableapp.vista

// Librerias de Android
import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.easytableapp.vista.EntregadoPantalla

import com.example.easytableapp.ui.EasyTableAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EasyTableAppTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "seleccionar_punto") {
//        Pantalla de seleccion de punto de venta
        composable("seleccionar_punto") {
            SeleccionPuntoPantalla(navController = navController)
        }
//        Pantalla de seleccion de mesa
        composable("seleccionar_mesa/{idPdv}") { backStackEntry ->
            val idPdv = backStackEntry.arguments?.getString("idPdv") ?: ""
            SeleccionMesaPantalla(navController = navController, idPdv = idPdv.toInt())
        }
//        Pantalla de detalles de la mesa seleccionada
        composable("detalles_mesa/{idMesa}") { backStackEntry ->
            val idMesa = backStackEntry.arguments?.getString("idMesa") ?: ""
            DetallesMesaPantalla(navController = navController, idMesa = idMesa.toInt())
        }

        // Pantalla para ver resumen de la mesa
        composable("resumen_mesa/{idMesa}") { backStackEntry ->
            val idMesa = backStackEntry.arguments?.getString("idMesa") ?: ""
            ResumenMesaPantalla(navController = navController, idMesa = idMesa.toInt())
        }

        // Pantalla para agregar productos a un comensal
        composable("agregar_productos/{idComensal}/{idMesa}") { backStackEntry ->
            val idComensal = backStackEntry.arguments?.getString("idComensal") ?: ""
            val idMesa = backStackEntry.arguments?.getString("idMesa") ?: ""
            ListaCategorias(navController = navController, idMesa = idMesa.toInt(), idComensal = idComensal.toInt())
        }

        //Pantalla para eliminar productos
        composable("eliminar_productos/{idComensal}") { backStackEntry ->
            val idComensal = backStackEntry.arguments?.getString("idComensal") ?: ""
            EliminarProductosPantalla(navController = navController, idComensal = idComensal.toInt())
        }

        //Pantalla para ver los productos de una categoria
        composable("ver_productos/{idCategoria}/{idMesa}/{idComensal}") { backStackEntry ->
            val idCategoria = backStackEntry.arguments?.getString("idCategoria") ?: ""
            val idMesa = backStackEntry.arguments?.getString("idMesa") ?: ""
            val idComensal = backStackEntry.arguments?.getString("idComensal") ?: ""
            ListaProductos(navController = navController, idCategoria = idCategoria.toInt(), idMesa = idMesa.toInt(), idComensal = idComensal.toInt())
        }

        // Pantalla para ver los extras de un producto
        composable("ver_extras/{idProducto}/{idMesa}/{idComensal}") { backStackEntry ->
            val idProducto = backStackEntry.arguments?.getString("idProducto") ?: ""
            val idMesa = backStackEntry.arguments?.getString("idMesa") ?: ""
            val idComensal = backStackEntry.arguments?.getString("idComensal") ?: ""
            ListaExtras(navController = navController, idProducto = idProducto.toInt(), idMesa = idMesa.toInt(), idComensal = idComensal.toInt())
        }

//       Pantalla para ver el resumen de un comensal
        composable("resumen_comensal/{idComensal}") { backStackEntry ->
            val idComensal = backStackEntry.arguments?.getString("idComensal") ?: ""
            ResumenComensalPantalla(navController = navController, idComensal = idComensal.toInt())
        }

//      Pantalla para eliminar comensales
        composable("eliminar_comensal/{idMesa}") { backStackEntry ->
            val idMesa = backStackEntry.arguments?.getString("idMesa") ?: ""
            EliminarComensalesPantalla(navController = navController, idMesa = idMesa.toInt())
        }

//      Pantalla para seleccionar la forma de pago
        composable("forma_pago/{idMesa}") { backStackEntry ->
            val idMesa = backStackEntry.arguments?.getString("idMesa") ?: ""
            SeleccionFormaPago(navController = navController, mesaId = idMesa.toInt())
        }

//      Pantalla para pagar la cuenta
        composable("pago/{idMesa}/{flag}"){ backStackEntry ->
            val idMesa = backStackEntry.arguments?.getString("idMesa") ?: ""
            val flag = backStackEntry.arguments?.getString("flag") ?: ""
            PagoPantalla(navController = navController, idMesa = idMesa.toInt(), flag = flag.toInt())
        }
//      Pantalla para marcar productos entregados
        composable("entregado_pantalla/{idMesa}") { backStackEntry ->
            val idMesa = backStackEntry.arguments?.getString("idMesa") ?: ""
            EntregadoPantalla(navController = navController, idMesa = idMesa.toInt())
        }

    }
}