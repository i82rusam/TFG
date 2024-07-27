package com.example.tfg.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tfg.R
import com.example.tfg.models.Inmueble
import com.google.firebase.firestore.FirebaseFirestore

class InmuebleDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_INMUEBLE_ID = "com.example.tfg.EXTRA_INMUEBLE_ID"
        const val EXTRA_INMUEBLE = "com.example.tfg.EXTRA_INMUEBLE"
    }

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inmueble_detail)

        val inmuebleId = intent.getStringExtra(EXTRA_INMUEBLE_ID)
        if (inmuebleId != null) {
            fetchInmuebleDetails(inmuebleId)
        } else {
            Toast.makeText(this, "Inmueble ID is missing", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchInmuebleDetails(inmuebleId: String) {
        firestore.collection("inmuebles").document(inmuebleId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val inmueble = document.toObject(Inmueble::class.java)
                    if (inmueble != null) {
                        displayInmuebleDetails(inmueble)
                    } else {
                        Log.e("InmuebleDetailActivity", "Inmueble is null")
                    }
                } else {
                    Log.e("InmuebleDetailActivity", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("InmuebleDetailActivity", "Error fetching inmueble details", exception)
            }
    }

    private fun displayInmuebleDetails(inmueble: Inmueble) {
        val textViewNombre: TextView = findViewById(R.id.textViewNombre)
        val textViewCiudad: TextView = findViewById(R.id.textViewCiudad)
        val textViewAlquilado: TextView = findViewById(R.id.textViewAlquilado)
        val textViewUbicacion: TextView = findViewById(R.id.textViewUbicacion)
        val textViewCodigoPostal: TextView = findViewById(R.id.textViewCodigoPostal)
        val textViewDocumento: TextView = findViewById(R.id.textViewDocumento)
        val imageViewInmueble = findViewById<ImageView>(R.id.imageViewInmueble)

        textViewNombre.text = getString(R.string.nombre_inmueble, inmueble.nombre)
        textViewCiudad.text = getString(R.string.ciudad_inmueble, inmueble.ciudad)
        textViewAlquilado.text = getString(R.string.alquilado_inmueble, inmueble.alquilado.toString())
        textViewUbicacion.text = getString(R.string.ubicacion_inmueble, inmueble.ubicacion)
        textViewCodigoPostal.text = getString(R.string.codigoPostal_inmueble, inmueble.codigoPostal)
        textViewDocumento.text = getString(R.string.escritura_inmueble, inmueble.escritura)

        inmueble.imagen?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .into(imageViewInmueble)
        }
    }
}