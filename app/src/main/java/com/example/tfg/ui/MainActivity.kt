package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_inmueble)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)


        //Inicializa Botón Añadir Inmueble
        val btnAddInmueble: Button = findViewById(R.id.btnAddInmueble)
        btnAddInmueble.setOnClickListener { view ->
            val intent = Intent(this, AgregarInmuebleActivity::class.java)
            startActivity(intent)
        }

        //Inicializa Botón Mis Inmuebles
        val btnMyInmuebles: Button = findViewById(R.id.btnMyInmuebles)
        btnMyInmuebles.setOnClickListener { view->
            val intent = Intent(this, TusInmueblesActivity::class.java)
            startActivity(intent)
        }

        // Inicialización del botón guardar
        val btnGuardar: Button = findViewById(R.id.btnGuardar)
        Log.d("agregarInmuebleActivity", "Función botón MainActivity")

        val intent = Intent(this, AgregarInmuebleActivity::class.java)
        startActivity(intent)

    }
}
