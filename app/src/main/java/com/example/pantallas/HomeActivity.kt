package com.example.pantallas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pantallas.model.Libro
import com.example.pantallas.model.LibroResponse
import com.example.pantallas.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    private lateinit var contenedorRecomendaciones: LinearLayout
    private lateinit var contenedorContinuar: LinearLayout
    private lateinit var btnComedia: Button
    private lateinit var btnTerror: Button
    private lateinit var btnRomance: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Conectar los contenedores del XML
        contenedorRecomendaciones = findViewById(R.id.contenedorRecomendaciones)
        contenedorContinuar = findViewById(R.id.contenedorContinuar)

        // Conectar los botones de géneros
        btnComedia = findViewById(R.id.btnComedia)
        btnTerror = findViewById(R.id.btnTerror)
        btnRomance = findViewById(R.id.btnRomance)

        // Cargar libros al iniciar la aplicación
        obtenerLibros("fiction")

        // Buscar libros de comedia
        btnComedia.setOnClickListener {
            obtenerLibros("comedy")
        }

        // Buscar libros de terror
        btnTerror.setOnClickListener {
            obtenerLibros("horror")
        }

        // Buscar libros de romance
        btnRomance.setOnClickListener {
            obtenerLibros("romance")
        }
    }

    // Obtener libros de la API
    private fun obtenerLibros(busqueda: String) {
        RetrofitClient.api.obtenerLibros(busqueda = busqueda, limite = 10).enqueue(object : Callback<LibroResponse> {

            override fun onResponse(call: Call<LibroResponse>, response: Response<LibroResponse>) {
                val libros = response.body()?.docs ?: emptyList()

                if (response.isSuccessful && libros.isNotEmpty()) {
                    mostrarLibros(libros, contenedorRecomendaciones)
                    mostrarLibros(libros.take(3), contenedorContinuar)
                } else {
                    Toast.makeText(this@HomeActivity, "No se encontraron libros", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LibroResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en el API: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Mostrar libros dentro del contenedor correspondiente
    private fun mostrarLibros(libros: List<Libro>, contenedor: LinearLayout) {
        contenedor.removeAllViews()

        for (libro in libros) {
            val vistaLibro = layoutInflater.inflate(R.layout.item_book, contenedor, false)

            val imgPortada = vistaLibro.findViewById<ImageView>(R.id.imgPortadaLibro)
            val tvNombreLibro = vistaLibro.findViewById<TextView>(R.id.tvNombreLibro)
            val tvNombreAutor = vistaLibro.findViewById<TextView>(R.id.tvNombreAutor)

            tvNombreLibro.text = libro.title ?: "Libro sin título"
            tvNombreAutor.text = libro.authorName?.joinToString(", ") ?: "Autor desconocido"

            Glide.with(this@HomeActivity)
                .load(libro.obtenerImagenGrande())
                .placeholder(R.color.black)
                .error(R.color.black)
                .into(imgPortada)

            // Evento de clic para abrir la pantalla de detalles y enviar la información

            vistaLibro.setOnClickListener {

                val intent = Intent(this@HomeActivity, DetailsActivity::class.java)

                intent.putExtra("ID_LIBRO", libro.key ?: "")
                intent.putExtra("TITULO_LIBRO", libro.title ?: "Libro sin titulo")
                intent.putExtra("AUTOR_LIBRO", libro.authorName?.joinToString(", ") ?: "Desconocido")
                intent.putExtra("ANIO_LIBRO", libro.firstPublishYear ?: 0)
                intent.putExtra("IMAGEN_LIBRO", libro.obtenerImagenGrande())

                startActivity(intent)
            }


            contenedor.addView(vistaLibro)
        }
    }
}