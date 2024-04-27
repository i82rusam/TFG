package com.example.tfg.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble
import com.google.firebase.auth.FirebaseAuth

class AgregarInmuebleActivity : AppCompatActivity() {

    private lateinit var repository: FirebaseRepository
    private lateinit var auth: FirebaseAuth
    private var idAleatorio: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_inmueble)

        repository = FirebaseRepository()
        //Inicializo auth
        auth = FirebaseAuth.getInstance()
        // Genero un ID único aleatorio
        idAleatorio = View.generateViewId()
        findViewById<EditText>(R.id.editTextAlquilado).id = idAleatorio

        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        btnGuardar.setOnClickListener {
            guardarInmueble()
        }
    }

    private fun guardarInmueble() {
        val alquilado = findViewById<EditText>(R.id.editTextAlquilado).text.toString().toIntOrNull() ?: 0
        val ciudad = findViewById<EditText>(R.id.editTextCiudad).text.toString()
        val escritura = findViewById<EditText>(R.id.btnCargarDocumento).text.toString()
        val imagen = findViewById<EditText>(R.id.btnCargarImagen).text.toString()
        val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val ubicacion = findViewById<EditText>(R.id.editTextUbicacion).text.toString()

        val usuarioActual = auth.currentUser?.displayName ?: "Nombre de usuario predeterminado"

        val inmueble = Inmueble(alquilado, ciudad, escritura, idAleatorio.toString(), imagen, nombre, ubicacion, usuarioActual)

        repository.agregarInmueble(inmueble,
            onSuccess = {
                Toast.makeText(this, "Inmueble añadido correctamente", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Toast.makeText(this, "Error al añadir el inmueble: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}