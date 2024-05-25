package com.example.tfg.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R

class InmuebleDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inmueble_detail)

        val nombre = intent.getStringExtra("nombre")
        val ciudad = intent.getStringExtra("ciudad")
        val alquilado = intent.getIntExtra("alquilado", 0)
        val ubicacion = intent.getStringExtra("ubicacion")
        val documento = intent.getStringExtra("documento")
        val imagen = intent.getStringExtra("imagen")
        val usuario = intent.getStringExtra("usuario")

        val textViewNombre: TextView = findViewById(R.id.textViewNombre)
        val textViewCiudad: TextView = findViewById(R.id.textViewCiudad)
        val textViewAlquilado: TextView = findViewById(R.id.textViewAlquilado)
        val textViewUbicacion: TextView = findViewById(R.id.textViewUbicacion)
        val textViewDocumento: TextView = findViewById(R.id.textViewDocumento)
        val textViewImagen: TextView = findViewById(R.id.textViewImagen)
        val textViewUsuario: TextView = findViewById(R.id.textViewUsuario)

        textViewNombre.text = nombre
        textViewCiudad.text = ciudad
        textViewAlquilado.text = alquilado.toString()
        textViewUbicacion.text = ubicacion
        textViewDocumento.text = documento
        textViewImagen.text = imagen
        textViewUsuario.text = usuario


    }
}
