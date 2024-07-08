package com.example.tfg.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble
import com.example.tfg.viewmodels.InmuebleViewModel
import com.example.tfg.viewmodels.InmuebleViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class AgregarInmuebleActivity : AppCompatActivity() {

    private lateinit var repository: FirebaseRepository
    private lateinit var auth: FirebaseAuth

    private val viewModel: InmuebleViewModel by viewModels { InmuebleViewModelFactory(FirebaseRepository(this)) }

    private var documentUri: Uri? = null
    private var imageUri: Uri? = null
    private var inmueble: Inmueble? = null

    private val documentResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        documentUri = uri
        if (uri != null) {
            Toast.makeText(this, getString(R.string.document_loaded, uri.toString()), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se seleccionó ningún documento", Toast.LENGTH_SHORT).show()
        }
    }

    private val imageResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        if (uri != null) {
            Toast.makeText(this, getString(R.string.image_loaded, uri.toString()), Toast.LENGTH_SHORT).show()
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
        Log.d("AgregarInmuebleActivity", "Función botón llamada1")
        repository = FirebaseRepository(this)

        val btnCargarDocumento: Button = findViewById(R.id.btnCargarDocumento)
        btnCargarDocumento.setOnClickListener { _ ->
            documentResultLauncher.launch("*/*")
        }

        val btnCargarImagen: Button = findViewById(R.id.btnCargarImagen)
        btnCargarImagen.setOnClickListener { _ ->
            imageResultLauncher.launch("image/*")
        }

        val btnGuardar: Button = findViewById(R.id.btnGuardar)
        btnGuardar.setOnClickListener { _ ->
            Log.d("AgregarInmuebleActivity", "btnGuardar onClickListener llamado")
            guardarInmueble()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_inmueble_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnEliminar -> {
                inmueble?.let {
                    eliminarInmueble(it.idInmueble, {
                        Toast.makeText(this, "Inmueble eliminado correctamente", Toast.LENGTH_SHORT).show()
                    }, { e ->
                        Toast.makeText(this, "Error al eliminar el inmueble: ${e.message}", Toast.LENGTH_SHORT).show()
                    })
                } ?: Toast.makeText(this, "Inmueble no seleccionado o inexistente", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun eliminarInmueble(idInmueble: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val localInmueble = inmueble
        if (localInmueble != null) {
            val idInmueble = localInmueble.idInmueble
            viewModel.eliminarInmueble(idInmueble,
                onSuccess = {
                    Toast.makeText(this, "Inmueble eliminado correctamente", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e: Exception ->
                    Toast.makeText(this, "Error al eliminar el inmueble: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(this, "No se puede eliminar un inmueble inexistente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarInmueble() {
        Log.d("AgregarInmuebleActivity", "Función guardarInmueble llamada")

        // Obtiene la instancia actual de FirebaseAuth y el usuario actual
        val usuarioActual = FirebaseAuth.getInstance().currentUser ?: return

        // Comprueba que el usuario está autenticado
        if (usuarioActual == null) {
            AlertDialog.Builder(this).apply {
                setTitle("Autenticación requerida")
                setMessage("Debe estar autenticado para agregar un inmueble. ¿Desea iniciar sesión ahora?")
                setPositiveButton("Iniciar sesión") { dialog, which ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                setNegativeButton("Cancelar", null)
                show()
            }
            return
        }

        val alquilado = findViewById<EditText>(R.id.editTextAlquilado).text.toString().toIntOrNull() ?: 0
        val ciudad = findViewById<EditText>(R.id.editTextCiudad).text.toString()
        val documentUriString = documentUri?.toString() ?: "a"
        val imageUriString = imageUri?.toString() ?: "a"
        val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val ubicacion = findViewById<EditText>(R.id.editTextUbicacion).text.toString()

        Log.d("AgregarInmuebleActivity", "Datos recogidos: alquilado=$alquilado, ciudad=$ciudad, nombre=$nombre, ubicacion=$ubicacion")

        val idAleatorio = UUID.randomUUID().toString()

        val inmueble = Inmueble(alquilado, ciudad, documentUriString, idAleatorio, imageUriString, nombre, ubicacion, usuarioActual.uid)

        Log.d("AgregarInmuebleActivity", "Inmueble creado: $inmueble")

        repository.agregarInmueble(inmueble,
            onSuccess = {
                Toast.makeText(this, "Inmueble añadido correctamente", Toast.LENGTH_SHORT).show()

                // Limpia los campos después de guardar
                findViewById<EditText>(R.id.editTextAlquilado).setText("")
                findViewById<EditText>(R.id.editTextCiudad).setText("")
                findViewById<EditText>(R.id.editTextNombre).setText("")
                findViewById<EditText>(R.id.editTextUbicacion).setText("")

                // Redirige a MainActivity
                val intent = Intent(this, TusInmueblesActivity::class.java)
                startActivity(intent)
                finish() // Opcional: Llama a finish() si deseas sacar esta actividad de la pila de actividades
            },
            onFailure = { e ->
                Toast.makeText(this, "Error al añadir el inmueble: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
