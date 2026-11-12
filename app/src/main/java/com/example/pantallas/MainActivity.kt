package com.example.pantallas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnIngresar : Button

    private lateinit var dbHelper: DataBaseHelper

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var btnGoogle: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPass)
        btnIngresar = findViewById(R.id.btnIngresar)

        dbHelper = DataBaseHelper(this)

        btnGoogle = findViewById(R.id.btnGoogle)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        //EVENTO PARA INICIAR SESION
        btnIngresar.setOnClickListener {
            //Llamar al metodo para iniciar sesion
            iniciarSesion()

        }

        btnGoogle.setOnClickListener {
            iniciarSesionGoogle()
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



    // 3. MÉTODO PARA AUTENTICAR EN FIREBASE
    private fun autenticaFirebase(idToken: String) {

        val credencialFirebase = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credencialFirebase).addOnCompleteListener(this) { inicio ->

            // Validar si el inicio de sesión es correcto
            if (inicio.isSuccessful) {

                // Crear variable para guardar al usuario actual
                val usuario = auth.currentUser
                Toast.makeText(this, "Bienvenido ${usuario?.displayName}", Toast.LENGTH_SHORT).show()

                // Enviar al usuario a HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)

                finish()
            } else {
                // Manejar error si falla la autenticación
                Toast.makeText(this, "Error al iniciar con Firebase: ${inicio.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    // 4. MÉTODO PARA PROCESAR LA CREDENCIAL
    private fun procesarCredencial(credential: Credential){

        if(credential is CustomCredential && credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){

            try {

                // validar la credencial del provedor
                val credencialGoogle = GoogleIdTokenCredential.createFrom(
                    credential.data
                )

                // iniciar proceso de autenticacion
                autenticaFirebase(credencialGoogle.idToken)

            }catch (e : GoogleIdTokenParsingException){
                Toast.makeText(this, "Credencial Invalida", Toast.LENGTH_SHORT).show()

            }
        }
    }


    // 5. MÉTODO PARA INICIAR SESIÓN
    private fun iniciarSesionGoogle(){

        val inicioGoogle = GetSignInWithGoogleOption.Builder(
            getString(R.string.default_web_client_id)).build()

        val solicitud = GetCredentialRequest.Builder()
            .addCredentialOption(inicioGoogle)
            .build()

        // proceso para iniciar con firebase
        lifecycleScope.launch {

            try{

                // verificar que el resultado sea correcto
                val resultado = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = solicitud

                )

                // llamar al metodo que permite procesar la credencial
                procesarCredencial(resultado.credential)

            }catch(e: GetCredentialException){
                Toast.makeText(this@MainActivity, "Error al iniciar", Toast.LENGTH_SHORT).show()
            }

        }
    }

}