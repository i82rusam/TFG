package com.example.tfg.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble
import com.example.tfg.viewmodels.InmuebleViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class AgregarInmuebleActivity : AppCompatActivity() {

    private lateinit var repository: FirebaseRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: InmuebleViewModel


    private var documentUri: Uri? = null
    private var imageUri: Uri? = null
    private var inmueble: Inmueble? = null

    private val documentResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        documentUri = uri
        if (uri != null) {
            Toast.makeText(this, "Documento cargado: $uri", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se seleccionó ningún documento", Toast.LENGTH_SHORT).show()
        }
    }

    private val imageResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Este bloque de código se ejecutará cuando se seleccione un archivo
        imageUri = uri
        if (uri != null) {
            Toast.makeText(this, "Imagen cargada: $uri", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_READ_STORAGE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_inmueble)
        Log.d("agregarInmuebleActivity", "Función botón llamada1")

        repository = FirebaseRepository(this)
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this).get(InmuebleViewModel::class.java)


        val btnCargarDocumento: Button = findViewById(R.id.btnCargarDocumento)
        btnCargarDocumento.setOnClickListener { _ ->
            // Lanza el contrato para obtener contenido cuando se pulsa el botón
            documentResultLauncher.launch("*/*")
        }

        val btnCargarImagen: Button = findViewById(R.id.btnCargarImagen)
        btnCargarImagen.setOnClickListener { _ ->
            // Lanza el contrato para obtener contenido cuando se pulsa el botón
            imageResultLauncher.launch("image/*")
        }


        val btnGuardar: Button = findViewById(R.id.btnGuardar)
        btnGuardar.setOnClickListener { view ->
            Log.d("AgregarInmuebleActivity", "btnGuardar onClickListener llamado")
            guardarInmueble(view)
        }


    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_inmueble_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnEliminar -> {
                eliminarInmueble()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun eliminarInmueble() {
        val localInmueble = inmueble
        if (localInmueble != null) {
            val idInmueble = localInmueble.idInmueble
            viewModel.eliminarInmueble(idInmueble,
                onSuccess = {
                    Toast.makeText(this, "Inmueble eliminado correctamente", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Toast.makeText(this, "Error al eliminar el inmueble: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(this, "No se puede eliminar el inmueble: ID no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    fun guardarInmueble(view: View) {
        Log.d("AgregarInmuebleActivity", "Función guardarInmueble llamada")

        val alquilado = findViewById<EditText>(R.id.editTextAlquilado).text.toString().toIntOrNull() ?: 0
        val ciudad = findViewById<EditText>(R.id.editTextCiudad).text.toString()
        val documentUriString = documentUri?.toString() ?:"a"
        val imageUriString = imageUri?.toString() ?:"a"
        val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val ubicacion = findViewById<EditText>(R.id.editTextUbicacion).text.toString()
        val usuarioActual = auth.currentUser?.displayName ?: "Nombre de usuario predeterminado"


        Log.d("AgregarInmuebleActivity", "Datos recogidos: alquilado=$alquilado, ciudad=$ciudad, nombre=$nombre, ubicacion=$ubicacion")

        val idAleatorio = UUID.randomUUID().toString()

        val inmueble = Inmueble(alquilado, ciudad, documentUriString, idAleatorio, imageUriString, nombre, ubicacion, usuarioActual)

        Log.d("AgregarInmuebleActivity", "Inmueble creado: $inmueble")

        repository.agregarInmueble(inmueble,
            onSuccess = {
                Toast.makeText(this, "Inmueble añadido correctamente", Toast.LENGTH_SHORT).show()

                //Borrar los campos después de guardar
                findViewById<EditText>(R.id.editTextAlquilado).setText("")
                findViewById<EditText>(R.id.editTextCiudad).setText("")
                findViewById<EditText>(R.id.editTextNombre).setText("")
                findViewById<EditText>(R.id.editTextUbicacion).setText("")
            },
            onFailure = { e ->
                Toast.makeText(this, "Error al añadir el inmueble: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
        fun cargarDocumento(@SuppressLint("RestrictedApi") view: View) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                documentResultLauncher.launch("*/*")
            } else {
                Toast.makeText(this, "Permiso de lectura no concedido", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permiso de lectura concedido", Toast.LENGTH_SHORT).show()
                    documentResultLauncher.launch("*/*")
                } else {
                    Toast.makeText(this, "Permiso de lectura denegado", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}