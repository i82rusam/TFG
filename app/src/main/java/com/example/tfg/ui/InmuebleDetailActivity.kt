package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble

class InmuebleDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_INMUEBLE = "com.example.tfg.EXTRA_INMUEBLE"
    }

    private val firebaseRepository = FirebaseRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inmueble_detail)

        val inmueble = intent.getParcelableExtra<Inmueble>(EXTRA_INMUEBLE)

        val textViewNombre: TextView = findViewById(R.id.textViewNombre)
        val textViewCiudad: TextView = findViewById(R.id.textViewCiudad)
        val textViewAlquilado: TextView = findViewById(R.id.textViewAlquilado)
        val textViewUbicacion: TextView = findViewById(R.id.textViewUbicacion)
        val textViewCodigoPostal: TextView = findViewById(R.id.textViewCodigoPostal)
        val textViewDocumento: TextView = findViewById(R.id.textViewDocumento)
        val imageViewInmueble = findViewById<ImageView>(R.id.imageViewInmueble)

        textViewNombre.text = getString(R.string.nombre_inmueble, inmueble?.nombre)
        textViewCiudad.text = getString(R.string.ciudad_inmueble, inmueble?.ciudad)
        textViewAlquilado.text = getString(R.string.alquilado_inmueble, inmueble?.alquilado.toString())
        textViewUbicacion.text = getString(R.string.ubicacion_inmueble, inmueble?.ubicacion)
        textViewCodigoPostal.text = getString(R.string.codigoPostal_inmueble, inmueble?.codigoPostal)
        textViewDocumento.text = getString(R.string.escritura_inmueble, inmueble?.escritura)

        // Cargar la imagen del inmueble usando Glide
        inmueble?.imagen?.let { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .into(imageViewInmueble)
        }

        val buttonThreeDots: ImageView = findViewById(R.id.buttonThreeDots)
        buttonThreeDots.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.menu_inmueble_detail, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.btnEditar -> {
                        val intent = Intent(this, EditarInmuebleActivity::class.java)
                        intent.putExtra(EXTRA_INMUEBLE, inmueble)
                        startActivity(intent)
                        true
                    }
                    R.id.btnEliminar -> {
                        inmueble?.idInmueble?.let { id ->
                            AlertDialog.Builder(this)
                                .setTitle("Confirmación de borrado")
                                .setMessage("¿Estás seguro de que quieres borrar el inmueble?")
                                .setPositiveButton("Sí") { _, _ ->
                                    firebaseRepository.eliminarInmueble(id, {
                                        Toast.makeText(this, "Inmueble borrado con éxito", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, TusInmueblesActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
                                    }, { _ ->
                                        Toast.makeText(this, "Error al borrar el inmueble", Toast.LENGTH_SHORT).show()
                                    })
                                }
                                .setNegativeButton("No", null)
                                .show()
                        }
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_inmueble_detail, menu)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, TusInmueblesActivity::class.java)
        startActivity(intent)
        finish()
    }

}