package com.example.tfg.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble
import com.example.tfg.viewmodels.InmuebleViewModel
import com.example.tfg.viewmodels.InmuebleViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class EditarInmuebleActivity : AppCompatActivity() {

    private lateinit var repository: FirebaseRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var inmueble: Inmueble

    private val viewModel: InmuebleViewModel by viewModels {
        InmuebleViewModelFactory(
            FirebaseRepository(this)
        )
    }

    private var documentUri: Uri? = null
    private var imageUri: Uri? = null

    private val documentResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            documentUri = uri
            if (uri != null) {
                Toast.makeText(this, "Documento cargado: $uri", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se seleccionó ningún documento", Toast.LENGTH_SHORT).show()
            }
        }

    private val imageResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            if (uri != null) {
                Toast.makeText(this, "Imagen cargada: $uri", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_inmueble)

        repository = FirebaseRepository(this)
        auth = FirebaseAuth.getInstance()

        // Corrección del identificador para coincidir con el usado al enviar el objeto desde InmuebleDetailActivity
        val inmuebleIntent = intent.getParcelableExtra<Inmueble>("inmueble")
        if (inmuebleIntent != null) {
            inmueble = inmuebleIntent
        } else {
            Toast.makeText(this, "Error: Inmueble no encontrado", Toast.LENGTH_SHORT).show()
            finish()
        }

        val editTextNombre: EditText = findViewById(R.id.editTextNombre)
        val editTextCiudad: EditText = findViewById(R.id.editTextCiudad)
        val editTextUbicacion: EditText = findViewById(R.id.editTextUbicacion)
        val buttonCargarDocumento: Button = findViewById(R.id.btnCargarDocumento)
        val buttonCargarImagen: Button = findViewById(R.id.btnCargarImagen)
        val buttonActualizarInmueble: Button = findViewById(R.id.boton_guardar)

        editTextNombre.setText(inmueble.nombre)
        editTextCiudad.setText(inmueble.ciudad)
        editTextUbicacion.setText(inmueble.ubicacion)

        buttonCargarDocumento.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                documentResultLauncher.launch("application/pdf")
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        buttonCargarImagen.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                imageResultLauncher.launch("image/*")
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        buttonActualizarInmueble.setOnClickListener {
            actualizarInmueble()
        }
    }

    private fun actualizarInmueble() {
        val camposActualizados = hashMapOf<String, Any>()
        val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val ciudad = findViewById<EditText>(R.id.editTextCiudad).text.toString()
        val ubicacion = findViewById<EditText>(R.id.editTextUbicacion).text.toString()

        // Solo agregar al mapa los campos que han sido modificados
        if (nombre.isNotEmpty()) camposActualizados["nombre"] = nombre
        if (ciudad.isNotEmpty()) camposActualizados["ciudad"] = ciudad
        if (ubicacion.isNotEmpty()) camposActualizados["ubicacion"] = ubicacion
        documentUri?.let { camposActualizados["escritura"] = it.toString() }

        if (camposActualizados.isNotEmpty()) {
            inmueble.idInmueble?.let { id ->
                repository.actualizarInmueble(id, camposActualizados, {
                    Toast.makeText(this, "Inmueble actualizado con éxito", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, TusInmueblesActivity::class.java)
                    startActivity(intent)
                    finish()
                }, {
                    Toast.makeText(this, "Error al actualizar el inmueble", Toast.LENGTH_SHORT)
                        .show()
                })
            } ?: Toast.makeText(this, "ID del inmueble no encontrado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se han realizado cambios.", Toast.LENGTH_SHORT).show()
        }
    }
}