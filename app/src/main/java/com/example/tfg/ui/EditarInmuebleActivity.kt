package com.example.tfg.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble
import com.example.tfg.viewmodels.InmuebleViewModel
import com.example.tfg.viewmodels.InmuebleViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class EditarInmuebleActivity : AppCompatActivity() {

    private lateinit var repository: FirebaseRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var inquilinoSpinner: Spinner

    private val viewModel: InmuebleViewModel by viewModels { InmuebleViewModelFactory(FirebaseRepository(this)) }

    private var inmuebleId: String? = null
    private var inmueble: Inmueble? = null
    private var users: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_inmueble)

        auth = FirebaseAuth.getInstance()
        repository = FirebaseRepository(this)

        inquilinoSpinner = findViewById(R.id.spinnerInquilino)

        inmuebleId = intent.getStringExtra("INMUEBLE_ID")

        if (inmuebleId != null) {
            cargarInmueble(inmuebleId!!)
        } else {
            Toast.makeText(this, "Error al cargar el ID del inmueble", Toast.LENGTH_SHORT).show()
            finish()
        }

        val btnGuardar: Button = findViewById(R.id.boton_guardar)
        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        cargarUsuarios()
    }

    private fun cargarInmueble(id: String) {
        repository.getInmueble(id,
            onSuccess = { inmueble: Inmueble ->
                this.inmueble = inmueble
                llenarCampos(inmueble)
            },
            onFailure = { e: Exception ->
                Toast.makeText(this, "Error al cargar el inmueble: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        )
    }

    private fun llenarCampos(inmueble: Inmueble) {
        val alquiladoSwitch = findViewById<Switch>(R.id.switchAlquilado)
        val ciudadEditText = findViewById<EditText>(R.id.editTextCiudad)
        val nombreEditText = findViewById<EditText>(R.id.editTextNombre)
        val ubicacionEditText = findViewById<EditText>(R.id.editTextUbicacion)
        val codigoPostalEditText = findViewById<EditText>(R.id.editTextCodigoPostal)

        alquiladoSwitch.isChecked = inmueble.alquilado
        ciudadEditText.setText(inmueble.ciudad)
        nombreEditText.setText(inmueble.nombre)
        ubicacionEditText.setText(inmueble.ubicacion)
        codigoPostalEditText.setText(inmueble.codigoPostal)

        val inquilinoIndex = users.indexOf(inmueble.inquilino)
        if (inquilinoIndex >= 0) {
            inquilinoSpinner.setSelection(inquilinoIndex)
        }
    }

    private fun guardarCambios() {
        val alquiladoSwitch = findViewById<Switch>(R.id.switchAlquilado)
        val ciudadEditText = findViewById<EditText>(R.id.editTextCiudad)
        val nombreEditText = findViewById<EditText>(R.id.editTextNombre)
        val ubicacionEditText = findViewById<EditText>(R.id.editTextUbicacion)
        val codigoPostalEditText = findViewById<EditText>(R.id.editTextCodigoPostal)

        val alquilado = alquiladoSwitch.isChecked
        val ciudad = ciudadEditText.text.toString()
        val nombre = nombreEditText.text.toString()
        val ubicacion = ubicacionEditText.text.toString()
        val codigoPostal = codigoPostalEditText.text.toString()
        val inquilino = inquilinoSpinner.selectedItem.toString()

        val usuarioActual = auth.currentUser?.uid ?: return

        val inmuebleActualizado = Inmueble(alquilado, ciudad, inmueble?.escritura ?: "", inmuebleId ?: UUID.randomUUID().toString(), inmueble?.imagen ?: "", nombre, ubicacion, codigoPostal, usuarioActual, inquilino)

        repository.actualizarInmueble(inmuebleId!!, inmuebleActualizado,
            onSuccess = {
                Toast.makeText(this, "Inmueble actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            },
            onFailure = { e: Exception ->
                Toast.makeText(this, "Error al actualizar el inmueble: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun cargarUsuarios() {
        repository.getUsers(
            onSuccess = { usersList ->
                users = usersList
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, users)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                inquilinoSpinner.adapter = adapter
            },
            onFailure = { e: Exception ->
                Toast.makeText(this, "Error al cargar los usuarios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}