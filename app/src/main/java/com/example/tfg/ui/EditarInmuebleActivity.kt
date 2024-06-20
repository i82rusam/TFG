package com.example.tfg.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble

class EditarInmuebleActivity : AppCompatActivity() {

    private lateinit var inmueble: Inmueble
    private lateinit var repository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_inmueble)

        repository = FirebaseRepository(this)

        // Obtener los datos del inmueble del intent
        inmueble = intent.getParcelableExtra<Inmueble>("inmueble")!!

        // Referenciar las vistas
        val editTextNombre: EditText = findViewById(R.id.editTextNombre)
        val editTextCiudad: EditText = findViewById(R.id.editTextCiudad)
        val editTextAlquilado: EditText = findViewById(R.id.editTextAlquilado)
        val editTextUbicacion: EditText = findViewById(R.id.editTextUbicacion)
        val editTextDocumento: EditText = findViewById(R.id.editTextDocumento)
        val editTextImagen: EditText = findViewById(R.id.editTextImagen)


        // Rellenar las vistas con los datos del inmueble
        editTextNombre.setText(inmueble.nombre)
        editTextCiudad.setText(inmueble.ciudad)
        editTextAlquilado.setText(inmueble.alquilado.toString())
        editTextUbicacion.setText(inmueble.ubicacion)
        editTextDocumento.setText(inmueble.escritura)
        editTextImagen.setText(inmueble.imagen)


        val buttonGuardar: Button = findViewById(R.id.boton_guardar)
        buttonGuardar.setOnClickListener {
            // Actualizar los datos del inmueble
            inmueble.nombre = editTextNombre.text.toString()
            inmueble.ciudad = editTextCiudad.text.toString()
            inmueble.alquilado = editTextAlquilado.text.toString().toInt()
            inmueble.ubicacion = editTextUbicacion.text.toString()
            inmueble.escritura = editTextDocumento.text.toString()
            inmueble.imagen = editTextImagen.text.toString()

            Log.d("EditarInmuebleActivity", "Datos del inmueble actualizados: $inmueble")


            // Guardar los cambios en Firebase
            repository.actualizarInmueble(inmueble, onSuccess = {
                // Volver a la actividad anterior
                Log.d("EditarInmuebleActivity", "Inmueble actualizado con éxito en Firebase")
                finish()
            }, onFailure = { e ->
                // Manejar el error
                Log.e("EditarInmuebleActivity", "Error al actualizar el inmueble en Firebase", e)
                e.printStackTrace()
            })
        }
    }
}
