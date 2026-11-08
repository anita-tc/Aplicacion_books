package com.example.pantallas

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {

    private lateinit var imgPortadaDetalle: ImageView
    private lateinit var btnFavorito: ImageButton
    private lateinit var tvTituloLibroDetalle: TextView
    private lateinit var tvAutorLibroDetalle: TextView
    private lateinit var tvDetallesLibro: TextView
    private var libroFavorito = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imgPortadaDetalle = findViewById(R.id.imgPortadaDetalle)
        btnFavorito = findViewById(R.id.btnFavorito)
        tvTituloLibroDetalle = findViewById(R.id.tvTituloLibroDetalle)
        tvAutorLibroDetalle = findViewById(R.id.tvAutorLibroDetalle)
        tvDetallesLibro = findViewById(R.id.tvDetallesLibro)

        mostrarInformacionLibro()
    }

    private fun mostrarInformacionLibro() {
        val idLibro = intent.getLongExtra("ID_LIBRO", 1)
        val tituloLibro = intent.getStringExtra("TITULO_LIBRO")
        val autor = intent.getStringExtra("AUTOR_LIBRO")
        val editorial = intent.getStringExtra("EDITORIAL_LIBRO")
        val paginas = intent.getIntExtra("PAGINAS_LIBRO", 0)
        val imagen = intent.getStringExtra("IMAGEN_LIBRO")

        tvTituloLibroDetalle.text = tituloLibro
        tvAutorLibroDetalle.text = autor
        tvDetallesLibro.text = "$editorial • $paginas págs."

        Glide.with(this)
            .load(imagen)
            .into(imgPortadaDetalle)

        libroFavorito = false
        btnFavorito.setImageResource(R.drawable.outline_favorite_24)

        btnFavorito.setOnClickListener {
            libroFavorito = !libroFavorito

            if (libroFavorito) {
                btnFavorito.setImageResource(R.drawable.baseline_favorite_24)
                Toast.makeText(this, "Libro '$tituloLibro' agregado a favoritos", Toast.LENGTH_SHORT).show()
            } else {
                btnFavorito.setImageResource(R.drawable.outline_favorite_24)
                Toast.makeText(this, "Libro '$tituloLibro' eliminado de favoritos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}