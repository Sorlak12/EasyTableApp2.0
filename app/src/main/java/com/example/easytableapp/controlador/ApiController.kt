package com.example.easytableapp.controlador

import android.util.Log
import com.example.easytableapp.modelo.Categoria
import com.example.easytableapp.modelo.Comensal
import com.example.easytableapp.modelo.Comensal_Producto
import com.example.easytableapp.modelo.Comensal_Producto_Extra
import com.example.easytableapp.modelo.Extra
import com.example.easytableapp.modelo.ExtraData
import com.example.easytableapp.modelo.Mesa
import com.example.easytableapp.modelo.Producto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
// Este objeto esta destinado a manejar todas las solicitudes
// que se hagan a la API de EasyTable

object ApiController {
    // Funcion que obtiene todos los comensales de una mesa
    fun obtenerComensalesPorMesa(
        idMesa: Int,
        onSuccess: (List<Comensal>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = RetrofitClient.api.getComensalesPorMesa(idMesa)
        call.enqueue(object : Callback<List<Comensal>> {
            override fun onResponse(
                call: Call<List<Comensal>>,
                response: Response<List<Comensal>>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body() ?: emptyList())
                }
            }

            override fun onFailure(call: Call<List<Comensal>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    // Funcion que obtiene el monto total de una mesa
    fun obtenerTotalMesa(idMesa: Int, onSuccess: (Int) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = RetrofitClient.api.getTotalMesa(idMesa)
        call.enqueue(object : Callback<List<Map<String, String>>> {
            override fun onResponse(
                call: Call<List<Map<String, String>>>,
                response: Response<List<Map<String, String>>>
            ) {
                if (response.isSuccessful) {
                    val total = response.body()?.get(0)?.get("TotalMesa")?.toInt() ?: 0
                    onSuccess(total)
                }
            }

            override fun onFailure(call: Call<List<Map<String, String>>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    // Funcion para agregar nuevo comensal
    fun agregarComensal(
        nombreComensal: String,
        idMesa: Int,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = RetrofitClient.api.postAgregarComensal(nombreComensal, idMesa)
        call.enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(
                call: Call<Map<String, String>>,
                response: Response<Map<String, String>>
            ) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Log.e("API", "Error en la respuesta: ${response.errorBody()?.string()}")
                    onFailure(Exception("Error en la respuesta"))
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    fun obtenerTotalComensal(
        idComensal: Int,
        onSuccess: (Int) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = RetrofitClient.api.getTotalComensal(idComensal)
        call.enqueue(object : Callback<List<Map<String, String>>> {
            override fun onResponse(
                call: Call<List<Map<String, String>>>,
                response: Response<List<Map<String, String>>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    // Verifica si el cuerpo de la respuesta no es nulo o vacío
                    if (!body.isNullOrEmpty()) {
                        // Si el cuerpo no es nulo, obtiene el total del primer elemento
                        val total = body[0]["TotalPedido"]?.toInt() ?: 0
                        onSuccess(total)
                    } else {
                        // Si el cuerpo es nulo o vacío, lanza una excepción
                        onFailure(Exception("Lista vacía"))
                    }
                } else {
                    onFailure(Exception("Error en la respuesta: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<Map<String, String>>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    // Obtener categorias
    fun obtenerCategorias(idMesa: Int, onSuccess: (MutableList<Categoria>) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = RetrofitClient.api.getCategorias(idMesa)
        call.enqueue(object : Callback<List<Categoria>> {
            override fun onResponse(
                call: Call<List<Categoria>>,
                response: Response<List<Categoria>>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.toMutableList() ?: mutableListOf())
                } else {
                    onFailure(
                        Exception(
                            "Error al obtener categorías: ${response.code()} - ${
                                response.errorBody()?.string()
                            }"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<List<Categoria>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    // Obtener categorias por IDPDV
    fun obtenerCategoriasPDV(idPDV: Int, onSuccess: (MutableList<Categoria>) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = RetrofitClient.api.getCategoriasPorPDV(idPDV)
        call.enqueue(object : Callback<List<Categoria>> {
            override fun onResponse(
                call: Call<List<Categoria>>,
                response: Response<List<Categoria>>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.toMutableList() ?: mutableListOf())
                } else {
                    onFailure(
                        Exception(
                            "Error al obtener categorías: ${response.code()} - ${
                                response.errorBody()?.string()
                            }"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<List<Categoria>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    fun obtenerProductosPorCategoria(
        idCategoria: Int,
        onSuccess: (List<Producto>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = RetrofitClient.api.getProductosPorCategoria(idCategoria)
        call.enqueue(object : Callback<List<Producto>> {
            override fun onResponse(
                call: Call<List<Producto>>,
                response: Response<List<Producto>>
            ) {
                if (response.isSuccessful) {
                    onSuccess(response.body() ?: emptyList())
                } else {
                    onFailure(
                        Exception(
                            "Error al obtener productos: ${response.code()} - ${
                                response.errorBody()?.string()
                            }"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<List<Producto>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    fun obtenerProductoPorId(
        idProducto: Int,
        onSuccess: (Producto) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = RetrofitClient.api.getProductoPorId(idProducto)
        call.enqueue(object : Callback<Producto> {
            override fun onResponse(call: Call<Producto>, response: Response<Producto>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onFailure(
                        Exception(
                            "Error al obtener producto: ${response.code()} - ${
                                response.errorBody()?.string()
                            }"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<Producto>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    //función que obtiene los datos de una mesa por la ID
    fun obtenerMesaPorId(idMesa: Int, onSuccess: (Mesa) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = RetrofitClient.api.getMesaPorId(idMesa)
        call.enqueue(object : Callback<Mesa> {
            override fun onResponse(call: Call<Mesa>, response: Response<Mesa>) {
                if (response.isSuccessful) {
                    onSuccess(response.body() ?: Mesa(0, 0, 0, "L", 0, 0))
                }
            }

            override fun onFailure(call: Call<Mesa>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    fun obtenerExtras(onSuccess: (List<Extra>) -> Unit, onFailure: (Throwable) -> Unit) {
        val call = RetrofitClient.api.getExtras()
        call.enqueue(object : Callback<List<Extra>> {
            override fun onResponse(call: Call<List<Extra>>, response: Response<List<Extra>>) {
                if (response.isSuccessful) {
                    onSuccess(response.body() ?: emptyList())
                } else {
                    onFailure(
                        Exception(
                            "Error al obtener extras: ${response.code()} - ${
                                response.errorBody()?.string()
                            }"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<List<Extra>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    fun agregarProducto(
        idComensal: Int,
        idProducto: Int,
        cantidad: Int,
        entregado: Int,
        comentario: String,
        extras: List<ExtraData>,
        onSuccess: (Int) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        Log.e("APIController", "extras: $extras")
        val notas = if (comentario.isNullOrBlank()) " " else comentario
        val call = RetrofitClient.api.postAgregarProducto(
            idComensal = idComensal,
            idProducto = idProducto,
            cantidad = cantidad,
            entregado = entregado,
            notas = notas,
            extras = extras
        )
        call.enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(
                call: Call<Map<String, String>>,
                response: Response<Map<String, String>>
            ) {
                Log.d("API", "Código de respuesta: ${response.code()}")
                Log.d("API", "Cuerpo de respuesta: ${response.body().toString()}")
                if (response.isSuccessful) {
                    // Extract the "instancia" value from the response if available
                    val instanciaGenerada = response.body()?.get("instancia")?.toIntOrNull()
                    if (instanciaGenerada != null) {
                        onSuccess(instanciaGenerada)
                    } else {
                        onFailure(Exception("Instancia no encontrada en la respuesta"))
                    }
                } else {
                    onFailure(
                        Exception("Error en la respuesta: ${response.code()} - ${response.errorBody()?.string()}")
                    )
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    fun agregarExtra(
        idComensal: Int,
        idProducto: Int,
        instancia: Int, // 👈 nuevo parámetro requerido
        idExtra: Int,
        cantidad: Int,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = RetrofitClient.api.postAgregarExtra(
            idComensal,
            idProducto,
            instancia,
            idExtra,
            cantidad
        )

        call.enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(
                call: Call<Map<String, String>>,
                response: Response<Map<String, String>>
            ) {
                Log.d("API", "Código de respuesta: ${response.code()}")
                Log.d("API", "Cuerpo de respuesta: ${response.body().toString()}")
                if (response.isSuccessful) {
                    Log.d("API", "✅ Extra agregado")
                    onSuccess()
                } else {
                    onFailure(
                        Exception(
                            "❌ Error al agregar extra: ${response.code()} - ${
                                response.errorBody()?.string()
                            }"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Log.e("API", "❌ Error de red: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }


    // Funcion para eliminar un producto de un comensal
    fun eliminarProductoDeComensal(
        idProducto: Int,
        idComensal: Int,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = RetrofitClient.api.deleteEliminarProducto(idProducto, idComensal)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Log.e("API", "Error en la respuesta: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    fun eliminarProductoDeComensalConCantidad(
        idProducto: Int,
        idComensal: Int,
        notas: String,
        instancia: Int,
        cantidad: Int,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = RetrofitClient.api.deleteEliminarProductoConCantidad(
            idProducto, idComensal, notas, instancia, cantidad
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Log.e("API", "❌ Error en la respuesta: ${response.code()} - ${response.message()}")
                    onFailure(Throwable("Error en la respuesta: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "❌ Error de red: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }



    fun eliminarComensal(idComensal: Int, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        val call = RetrofitClient.api.deleteEliminarComensal(idComensal)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Log.e("API", "Error en la respuesta: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    //  Funcion para obtener los datos de un comensal especifico
    fun obtenerDatosComensal(
        idComensal: Int,
        onSuccess: (Comensal) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = RetrofitClient.api.getComensal(idComensal)
        call.enqueue(object : Callback<List<Comensal>> {
            override fun onResponse(
                call: Call<List<Comensal>>,
                response: Response<List<Comensal>>
            ) {
                if (response.isSuccessful) {
                    val comensal = response.body()
                    if (comensal != null) {
                        onSuccess(comensal[0])
                    }
                }
            }

            override fun onFailure(call: Call<List<Comensal>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    // Funcion para obtener los detalles de una mesa
    fun obtenerDetallesMesa(
        idMesa: Int,
        onSuccess: (List<Comensal>, List<Producto>, List<Extra>, List<Comensal_Producto>, List<Comensal_Producto_Extra>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = RetrofitClient.api.getDetallesMesa(idMesa)
        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()

                    val comensalesSet = mutableSetOf<Comensal>()
                    val productosSet = mutableSetOf<Producto>()
                    val extrasSet = mutableSetOf<Extra>()
                    val comensalProductoSet = mutableSetOf<Comensal_Producto>()
                    val comensalProductoExtraSet = mutableSetOf<Comensal_Producto_Extra>()

                    for (item in data) {
                        val comensal = Comensal(
                            IDComensal = (item["IDComensal"] as Double).toInt(),
                            NombreComensal = item["NombreComensal"] as String,
                            IDMesa = (item["IDMesa"] as Double).toInt()
                        )
                        comensalesSet.add(comensal)

                        val producto = Producto(
                            IDProducto = (item["IDProducto"] as Double).toInt(),
                            NombreProducto = item["NombreProducto"] as String,
                            ValorProducto = (item["ValorProducto"] as Double).toInt(),
                            IDCategoria = (item["IDCategoria"] as Double).toInt(),
                            IDPDV = (item["IDPDV"] as Double).toInt()
                        )
                        productosSet.add(producto)

                        val instancia = (item["Instancia"] as? Double)?.toInt() ?: 0
                        val comensalProducto = (item["Notas"] as? String)?.let {
                            Comensal_Producto(
                                IDComensal = (item["CP_IDComensal"] as Double).toInt(),
                                IDProducto = (item["CP_IDProducto"] as Double).toInt(),
                                cantidad = (item["Producto_Cantidad"] as Double).toInt(),
                                Notas = it,
                                entregado = (item["Producto_Entregado"] as Double).toInt() == 1,
                                Instancia = instancia

                            )
                        }
                        if (comensalProducto != null) {
                            comensalProductoSet.add(comensalProducto)
                        }

                        val idExtra = item["IDExtra"] as? Double
                        if (idExtra != null) {
                            val extra = Extra(
                                IDExtra = idExtra.toInt(),
                                NombreExtra = item["NombreExtra"] as? String,
                                ValorExtra = (item["ValorExtra"] as Double).toInt()
                            )
                            extrasSet.add(extra)

                            val instancia = (item["Instancia"] as? Double)?.toInt() ?: 0
                            val comensalProductoExtra = (item["Notas"] as? String)?.let {
                                Comensal_Producto_Extra(
                                    IDComensal = (item["CPE_IDComensal"] as Double).toInt(),
                                    IDProducto = (item["CPE_IDProducto"] as Double).toInt(),
                                    IDExtra = (item["CPE_IDExtra"] as Double).toInt(),
                                    cantidad = (item["Cantidad_Extra"] as Double).toInt(),
                                    Notas = it,
                                    Instancia = instancia
                                )
                            }
                            if (comensalProductoExtra != null) {
                                comensalProductoExtraSet.add(comensalProductoExtra)
                            }
                        }
                    }

                    onSuccess(
                        comensalesSet.toList(),
                        productosSet.toList(),
                        extrasSet.toList(),
                        comensalProductoSet.toList(),
                        comensalProductoExtraSet.toList()
                    )
                } else {
                    Log.e("API", "Error en la respuesta: ${response.errorBody()?.string()}")
                    onFailure(Exception("Error en la respuesta"))
                }
            }

            override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    // Funcion para obtener los pedidos de un comensal especifico
    fun obtenerPedidosComensal(
        idComensal: Int,
        onSuccess: (List<Producto>, List<Extra>, List<Comensal_Producto>, List<Comensal_Producto_Extra>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val call = RetrofitClient.api.getPedidosComensal(idComensal)
        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()

                    val productosSet = mutableSetOf<Producto>()
                    val extrasSet = mutableSetOf<Extra>()
                    val comensalProductoSet = mutableSetOf<Comensal_Producto>()
                    val comensalProductoExtraSet = mutableSetOf<Comensal_Producto_Extra>()


                    for (item in data) {
                        val producto = Producto(
                            IDProducto = (item["IDProducto"] as Double).toInt(),
                            NombreProducto = item["NombreProducto"] as String,
                            ValorProducto = (item["ValorProducto"] as Double).toInt(),
                            IDCategoria = (item["IDCategoria"] as Double).toInt(),
                            IDPDV = (item["IDPDV"] as Double).toInt()
                        )
                        productosSet.add(producto)

                        val instancia = (item["Instancia"] as? Double)?.toInt() ?: 0
                        val comensalProducto = (item["Notas"] as? String)?.let {
                            Comensal_Producto(
                                IDComensal = (item["CP_IDComensal"] as Double).toInt(),
                                IDProducto = (item["CP_IDProducto"] as Double).toInt(),
                                cantidad = (item["Producto_Cantidad"] as Double).toInt(),
                                Notas = it,
                                entregado = (item["Producto_Entregado"] as Double).toInt() == 1,
                                Instancia = instancia

                            )
                        }
                        if (comensalProducto != null) {
                            comensalProductoSet.add(comensalProducto)
                        }

                        val idExtra = item["IDExtra"] as? Double
                        if (idExtra != null) {
                            val extra = Extra(
                                IDExtra = idExtra.toInt(),
                                NombreExtra = item["NombreExtra"] as? String,
                                ValorExtra = (item["ValorExtra"] as Double).toInt()
                            )
                            extrasSet.add(extra)

                            val instancia = (item["Instancia"] as? Double)?.toInt() ?: 0
                            val comensalProductoExtra = Comensal_Producto_Extra(
                                IDComensal = (item["CPE_IDComensal"] as Double).toInt(),
                                IDProducto = (item["CPE_IDProducto"] as Double).toInt(),
                                IDExtra = (item["CPE_IDExtra"] as Double).toInt(),
                                cantidad = (item["Cantidad_Extra"] as Double).toInt(),
                                Notas = item["Notas"] as? String ?: " ",
                                Instancia = instancia
                            )
                            comensalProductoExtraSet.add(comensalProductoExtra)
                        }
                    }

                    // Retornar los datos
                    onSuccess(
                        productosSet.toList(),
                        extrasSet.toList(),
                        comensalProductoSet.toList(),
                        comensalProductoExtraSet.toList()
                    )
                } else {
                    Log.e("API", "Error en la respuesta: ${response.errorBody()?.string()}")
                    onFailure(Exception("Error en la respuesta"))
                }
            }

            override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    // Funcion para marcar comensal como pagado
    fun marcarComensalComoPagado(idComensal: Int, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        val call = RetrofitClient.api.putPagarComensal(idComensal)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Log.e("API", "Error en la respuesta: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    // Funcion para pagar una mesa
    fun marcarMesaComoPagada(idMesa: Int, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        val call = RetrofitClient.api.deletePagarMesa(idMesa)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Log.e("API", "Error en la respuesta: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error: ${t.localizedMessage}")
                onFailure(t)
            }
        })
    }

    fun actualizarEntregado(
        comensalProductoList: List<Comensal_Producto>,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        if (comensalProductoList.isEmpty()) {
            println("🟡 Lista vacía, nada que actualizar")
            onSuccess(true)
            return
        }
        println(comensalProductoList)
        var successCount = 0
        var failureCount = 0

        println("🟦 Procesando ${comensalProductoList.size} elementos...")

        comensalProductoList.forEachIndexed { index, comensalProducto ->
            val entregado = comensalProducto.entregado ?: false
            val rawNotas = comensalProducto.Notas.orEmpty()
            val safeNotas = if (rawNotas.isBlank()) "sin-nota" else rawNotas
            val instancia = comensalProducto.Instancia

            val notasEncoded = URLEncoder.encode(safeNotas, StandardCharsets.UTF_8.toString())
                .replace("+", "%20")
                .replace("%2F", "%252F")

            println("🔵 [$index] Enviando a la API:")
            println("     IDComensal: ${comensalProducto.IDComensal}")
            println("     IDProducto: ${comensalProducto.IDProducto}")
            println("     Notas (raw): '$rawNotas'")
            println("     Notas (encoded): '$notasEncoded'")
            println("     Entregado: $entregado")

            RetrofitClient.api.postActualizarEntregado(
                comensalProducto.IDComensal,
                comensalProducto.IDProducto,
                notasEncoded,
                instancia,
                entregado
            ).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    println("🟢 [$index] Respuesta recibida: ${response.code()}")
                    if (response.isSuccessful) {
                        successCount++
                    } else {
                        println("❌ [$index] Error de respuesta: ${response.code()} ${response.message()}")
                        failureCount++
                    }

                    if (successCount + failureCount == comensalProductoList.size) {
                        if (failureCount == 0) {
                            onSuccess(true)
                        } else {
                            onFailure(Exception("Algunos elementos fallaron"))
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    println("🔴 [$index] Fallo de red: ${t.localizedMessage}")
                    failureCount++

                    if (successCount + failureCount == comensalProductoList.size) {
                        onFailure(t)
                    }
                }
            })
        }
    }



}