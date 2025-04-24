package com.example.easytableapp.controlador

// Librerias de Retrofit
import com.example.easytableapp.modelo.Categoria
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// Data class necesarias
import com.example.easytableapp.modelo.Pdv
import com.example.easytableapp.modelo.Mesa
import com.example.easytableapp.modelo.Producto
import com.example.easytableapp.modelo.Comensal
import com.example.easytableapp.modelo.Extra
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    // Solicitud GET para obtener todos los PDV
    @GET("/pdv")
    fun getPDV(): Call<List<Pdv>>

    // Solicitud GET para obtener todas las mesas de un PDV
    @GET("/mesas/{idPdv}")
    fun getMesasPorPDV(@Path("idPdv") idPdv: Int): Call<List<Mesa>>

    @GET("/mesa/{idMesa}")
    fun getMesaPorId(@Path("idMesa") idMesa: Int): Call<Mesa>

    // Solicitud GET para obtener los detalles de una mesa
    @GET("/detallesmesa/{idMesa}")
    fun getDetallesMesa(@Path("idMesa") idMesa: Int): Call<List<Map<String, Any>>>

    // Solicitud GET para obtener todos los productos de una categoria
    @GET("/productos/categoria/{idCategoria}")
    fun getProductosPorCategoria(@Path("idCategoria") idCategoria: Int): Call<List<Producto>>

    //get productos por id
    @GET("/producto/{idProducto}")
    fun getProductoPorId(@Path("idProducto") idProducto: Int): Call<Producto>

    // Solicitud GET para obtener los extras
    @GET("/extras")
    fun getExtras(): Call<List<Extra>>

    //POST agregar producto a comensal
    @POST("/agregarProducto/{idComensal}/{idProducto}/{cantidad}/{entregado}/{notas}")
    fun postAgregarProducto(
        @Path("idComensal") idComensal: Int,
        @Path("idProducto") idProducto: Int,
        @Path("cantidad") cantidad: Int,
        @Path("entregado") entregado: Int,
        @Path("notas") notas: String ?= null
    ): Call<Map<String,String>>

    //POST agregar extra
    @POST("/agregarExtra/{idComensal}/{idProducto}/{notas}/{idExtra}/{cantidad}")
    fun postAgregarExtra(
        @Path("idComensal") idComensal: Int,
        @Path("idProducto") idProducto: Int,
        @Path("notas") notas: String,
        @Path("idExtra") idExtra: Int,
        @Path("cantidad") cantidad: Int
    ): Call<Map<String, String>>


    // Solicitud GET para obtener todos los comensales de una mesa
    @GET("/comensales/mesa/{idMesa}")
    fun getComensalesPorMesa(@Path("idMesa") idMesa: Int): Call<List<Comensal>>

    // Solicitud GET para obtener el monto total de una mesa
    @GET("/totalMesa/mesa/{idMesa}")
    fun getTotalMesa(@Path("idMesa") idMesa: Int): Call<List<Map<String, String>>>

    // Solicitud GET para obtener el monto total de un comensal
    @GET("/totalComensal/comensal/{idComensal}")
    fun getTotalComensal(@Path("idComensal") idComensal: Int): Call<List<Map<String, String>>>

    // Solicitud GET para obtener la informacion de un comensal
    @GET("/comensal/{idComensal}")
    fun getComensal(@Path("idComensal") idComensal: Int): Call<List<Comensal>>

    // Solicitud GET para obtener los pedidos de un comensal
    @GET("/comensal/productos/{idComensal}")
    fun getPedidosComensal(@Path("idComensal") idComensal: Int): Call<List<Map<String, Any>>>

    // Solicitud GET para obtener las categorias
    @GET("/categorias/{idMesa}")
    fun getCategorias(@Path("idMesa") idMesa: Int ): Call<List<Categoria>>

    //get productos por pdv
    @GET("/productos/pdv/{idPDV}")
    fun getProductosPorPDV(@Path("idPDV") idPDV: Int): Call<List<Producto>>

    // Solicitud POST para agregar un comensal
    @POST("/agregarComensal/{nombreComensal}/{idMesa}")
    fun postAgregarComensal(
        @Path("nombreComensal") nombreComensal: String,
        @Path("idMesa") idMesa: Int
    ): Call<Map<String, String>>

    @DELETE("/eliminarComensal/{idComensal}")
    fun deleteEliminarComensal(@Path("idComensal") idComensal: Int): Call<Void>

    // Solicitud DELETE para eliminar un producto de un comensal
    @DELETE("/eliminarProducto/{idProducto}/{idComensal}")
    fun deleteEliminarProducto(
        @Path("idProducto") idProducto: Int,
        @Path("idComensal") idComensal: Int
    ): Call<Void>

    //elimiar producto de comensal con cantidad
    @DELETE("eliminarProductoConCantidad/{idProducto}/{idComensal}/{notas}/{cantidad}")
    fun deleteEliminarProductoConCantidad(
        @Path("idProducto") idProducto: Int,
        @Path("idComensal") idComensal: Int,
        @Path("notas") notas: String,
        @Path("cantidad") cantidad: Int
    ): Call<Void>


    // Solicitud PUT para marcar un comensal como pagado
    @PUT("/pagarComensal/{idComensal}")
    fun putPagarComensal(@Path("idComensal") idComensal: Int): Call<Void>

    // Solicitud DELETE para marcar una mesa como pagada
    @DELETE("/pagarMesa/{idMesa}")
    fun deletePagarMesa(@Path("idMesa") idMesa: Int): Call<Void>

    @POST("actualizar_entregado/{id_comensal}/{id_producto}/{notas}/{entregado}")
    fun postActualizarEntregado(
        @Path("id_comensal") idComensal: Int,
        @Path("id_producto") idProducto: Int,
        @Path("notas") notas: String,
        @Path("entregado") entregado: Boolean
    ): Call<Void>





}