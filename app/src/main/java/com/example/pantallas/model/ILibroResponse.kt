
package com.example.pantallas.model

// ARCHIVO PARA RECIBIR LA RESPUESTA DEL API
data class LibroResponse(
    val numFound: Int,
    val docs: List<Libro>
)
