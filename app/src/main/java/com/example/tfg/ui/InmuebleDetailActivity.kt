package com.example.tfg.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.models.Inmueble

class InmuebleDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_INMUEBLE = "EXTRA_INMUEBLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inmueble_detail)


        val inmueble = intent.getParcelableExtra<Inmueble>(EXTRA_INMUEBLE)

        val textViewNombre: TextView = findViewById(R.id.textViewNombre)
        val textViewCiudad: TextView = findViewById(R.id.textViewCiudad)
        val textViewAlquilado: TextView = findViewById(R.id.textViewAlquilado)
        val textViewUbicacion: TextView = findViewById(R.id.textViewUbicacion)
        val textViewDocumento: TextView = findViewById(R.id.textViewDocumento)
        val textViewImagen: TextView = findViewById(R.id.textViewImagen)
        val textViewUsuario: TextView = findViewById(R.id.textViewUsuario)

        textViewNombre.text = "Nombre: ${inmueble?.nombre}"
        textViewCiudad.text = "Ciudad: ${inmueble?.ciudad}"
        textViewAlquilado.text = "Alquilado: ${inmueble?.alquilado.toString()}"
        textViewUbicacion.text = "Ubicacion: ${inmueble?.ubicacion}"
        textViewDocumento.text = "Escritura: ${inmueble?.escritura}"
        textViewImagen.text = "Imagen: ${inmueble?.imagen}"
        textViewUsuario.text = "Usuario: ${inmueble?.usuario}"


        val titleTextView: TextView = findViewById(R.id.textView)
        titleTextView.setTextColor(Color.WHITE)
        titleTextView.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.menu_inmueble_detail, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        // Aquí manejas la acción de editar
                        true
                    }
                    R.id.action_delete -> {
                        // Aquí manejas la acción de borrar
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


}