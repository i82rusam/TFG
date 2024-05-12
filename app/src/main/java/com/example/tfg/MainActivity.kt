package com.example.tfg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.ui.AgregarInmuebleActivity
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_inmueble)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        // Inicialización del botón guardar
        val btnIniciar: Button = findViewById(R.id.btnGuardar)
        Log.d("agregarInmuebleActivity", "Función botón MainActivity")

        // Establecer un Listener para el evento de clic del botón guardar
        btnIniciar.setOnClickListener() {
            Log.d("MainActivity", "btnIniciar onClickListener llamado")

            val intent = Intent(this, AgregarInmuebleActivity::class.java)
            startActivity(intent)
        }


    }
}