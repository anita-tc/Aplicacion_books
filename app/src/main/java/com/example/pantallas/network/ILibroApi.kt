

package com.example.pantallas.network

import com.example.pantallas.model.LibroResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Archivo para consultar informacion de libros por internet
interface ILibroApi {

    // Indicar el metodo HTTP a usar
    @GET("search.json")

    // Metodo para obtener la informacion de los libros
    fun obtenerLibros(
        @Query("q") busqueda: String,
        @Query("limit") limite: Int
    ): Call<LibroResponse>
}

