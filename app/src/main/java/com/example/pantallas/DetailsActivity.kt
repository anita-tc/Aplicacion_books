
package com.example.pantallas

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {

    private lateinit var imgPortadaDetalle: ImageView
    private lateinit var btnFavorito: ImageButton
    private lateinit var tvTituloLibroDetalle: TextView
    private lateinit var tvAutorLibroDetalle: TextView
    private lateinit var tvDetallesLibro: TextView

    private lateinit var db: DataBaseHelper

    private var libroFavorito = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)

        imgPortadaDetalle = findViewById(R.id.imgPortadaDetalle)
        btnFavorito = findViewById(R.id.btnFavorito)
        tvTituloLibroDetalle = findViewById(R.id.tvTituloLibroDetalle)
        tvAutorLibroDetalle = findViewById(R.id.tvAutorLibroDetalle)
        tvDetallesLibro = findViewById(R.id.tvDetallesLibro)

        db = DataBaseHelper(this)

        mostrarInformacionLibro()
    }

    // Metodo para mostrar la informacion de un libro
    private fun mostrarInformacionLibro() {

        val idLibro = intent.getStringExtra("ID_LIBRO") ?: ""
        val titulo = intent.getStringExtra("TITULO_LIBRO") ?: "Libro sin titulo"
        val autor = intent.getStringExtra("AUTOR_LIBRO") ?: "Autor desconocido"
        val anio = intent.getIntExtra("ANIO_LIBRO", 0)
        val imagen = intent.getStringExtra("IMAGEN_LIBRO") ?: ""

        tvTituloLibroDetalle.text = titulo
        tvAutorLibroDetalle.text = autor
        tvDetallesLibro.text = if (anio != 0) {
            "Año de publicación: $anio"
        } else {
            "Año de publicación desconocido"
        }

        Glide.with(this)
            .load(imagen)
            .into(imgPortadaDetalle)

        // Verificar que el libro exista como favorito
        if (idLibro.isNotEmpty()) {
            libroFavorito = db.existeLibroFavorito(idLibro)
        }

        actualizarIconoFavorito()

        btnFavorito.setOnClickListener {

            if (idLibro.isEmpty()) {
                Toast.makeText(this, "No se puede guardar este libro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            libroFavorito = !libroFavorito

            if (libroFavorito) {

                val eliminar = db.eliminarLibroFavorito(idLibro)

                if (eliminar) {
                    libroFavorito = !libroFavorito
                    Toast.makeText(
                        this,
                        "Libro $titulo eliminado de favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {

                val guardado = db.guardarLibroFavorito(
                    libroId = idLibro,
                    titulo = titulo,
                    autor = autor,
                    anio = anio,
                    imagen = imagen
                )

                if (guardado) {
                    libroFavorito = !libroFavorito
                    Toast.makeText(
                        this,
                        "Libro $titulo agregado a favoritos",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(
                        this,
                        "El libro ya se encuentra agregado a favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            actualizarIconoFavorito()
        }
    }

    // Metodo para actualizar el icono del favorito
    private fun actualizarIconoFavorito() {

        if (libroFavorito) {
            btnFavorito.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            btnFavorito.setImageResource(R.drawable.outline_favorite_24)
        }
    }
}
