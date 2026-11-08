
package com.example.pantallas.model

import com.google.gson.annotations.SerializedName

// Clase para obtener la información de cada libro
data class Libro(

    val key: String?,

    val title: String?,

    @SerializedName("author_name")
    val authorName: List<String>?,

    @SerializedName("first_publish_year")
    val firstPublishYear: Int?,

    @SerializedName("cover_i")
    val coverId: Long?

) {
    // Método para obtener la imagen de la portada
    fun obtenerImagenGrande(): String {
        return coverId?.let {
            "https://covers.openlibrary.org/b/id/${it}-L.jpg"
        } ?: ""
    }
}
