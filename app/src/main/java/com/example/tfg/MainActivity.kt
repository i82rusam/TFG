package com.example.tfg

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.ui.AgregarInmuebleActivity
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        val aggInmueble= AgregarInmuebleActivity()
        Log.d("MainActivity", "Función main llamada1")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_inmueble)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        // Inicialización del botón guardar
        val btnGuardar: Button = findViewById(R.id.btnGuardar)
        Log.d("agregarInmuebleActivity", "Función botón llamada2")

        // Establecer un Listener para el evento de clic del botón guardar
        btnGuardar.setOnClickListener() {

            aggInmueble.guardarInmueble()
        }


    }
}