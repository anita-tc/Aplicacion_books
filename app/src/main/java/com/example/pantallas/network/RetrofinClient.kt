
package com.example.pantallas.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Archivo que permite crear el objeto para conectarse a internet
object RetrofitClient {

    // Constante para tener la BASE URL
    private const val BASE_URL = "https://openlibrary.org/"

    // Variable que crea la conexion cuando se necesita
    val api: ILibroApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ILibroApi::class.java)
    }
}
