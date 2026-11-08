package com.example.pantallas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnIngresar : Button

    private lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPass)
        btnIngresar = findViewById(R.id.btnIngresar)

        dbHelper = DataBaseHelper(this)

        //EVENTO PARA INICIAR SESION
        btnIngresar.setOnClickListener {
            //Llamar al metodo para iniciar sesion
            iniciarSesion()

        }


        tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegistroActivity:: class.java)
            startActivity(intent)
        }
    }

    //METODO PARA INICIAR SESION
    private fun iniciarSesion(){
        val correo = etCorreo.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if(correo.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //LLAMAR AL METODO DE INICIO DE SESION EN LA BD
        val resultado = dbHelper.inisiarSesion(correo,pass)

        //VALIDAR SI EL INICIO DE SESION FUE CORRECTO
        if (resultado){
            // EL RESULTADO ES VERDADERO
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, HomeActivity :: class.java)
            startActivity(intent)
            finish()
        }else{
            //EL RESULTADO ES FALSO
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
        }

    }
}