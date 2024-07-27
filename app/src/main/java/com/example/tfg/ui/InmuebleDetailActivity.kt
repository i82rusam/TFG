package com.example.tfg.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble

class InmuebleDetailActivity : AppCompatActivity() {

    private lateinit var repository: FirebaseRepository

    companion object {
        const val EXTRA_INMUEBLE_ID = "EXTRA_INMUEBLE_ID"
        private const val TAG = "InmuebleDetailActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inmueble_detail)

        repository = FirebaseRepository(this)

        val idInmueble = intent.getStringExtra(EXTRA_INMUEBLE_ID)
        Log.d(TAG, "Received idInmueble: $idInmueble")
        if (idInmueble != null) {
            fetchInmuebleDetails(idInmueble)
        } else {
            Toast.makeText(this, "Inmueble ID is missing", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchInmuebleDetails(idInmueble: String) {
        repository.getInmueble(idInmueble,
            onSuccess = { inmueble ->
                displayInmuebleDetails(inmueble)
            },
            onFailure = { e ->
                Log.e(TAG, "Error fetching inmueble details", e)
                Toast.makeText(this, "Error fetching inmueble details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun displayInmuebleDetails(inmueble: Inmueble) {
        Log.d(TAG, "Displaying details for inmueble: ${inmueble.idInmueble}")

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
        } ?: run {
            imageViewInmueble.setImageResource(R.drawable.ic_profile)
        }
    }
}