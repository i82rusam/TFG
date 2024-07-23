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
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble
import com.example.tfg.viewmodels.InmuebleViewModel
import com.example.tfg.viewmodels.InmuebleViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.util.UUID

class AgregarInmuebleActivity : AppCompatActivity() {

    private lateinit var repository: FirebaseRepository


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

    //companion object {
    //    private const val REQUEST_READ_STORAGE = 100
    //}

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
        btnCargarImagen.setOnClickListener {
            // Especifica directamente el tipo MIME para seleccionar imágenes
            imageResultLauncher.launch("image/*") // Corregido para usar un String directamente
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
        viewModel.eliminarInmueble(idInmueble,
            onSuccess = {
                Toast.makeText(this, "Inmueble eliminado correctamente", Toast.LENGTH_SHORT).show()
                onSuccess()
            },
            onFailure = { e: Exception ->
                Toast.makeText(this, "Error al eliminar el inmueble: ${e.message}", Toast.LENGTH_SHORT).show()
                onFailure(e)
            }
        )
    }

    private fun guardarInmueble() {
        Log.d("AgregarInmuebleActivity", "Función guardarInmueble llamada")

        // Obtiene la instancia actual de FirebaseAuth y el usuario actual
        val usuarioActual = FirebaseAuth.getInstance().currentUser ?: return

        if (usuarioActual == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val alquilado = findViewById<EditText>(R.id.editTextAlquilado).text.toString().toIntOrNull() ?: 0
        val ciudad = findViewById<EditText>(R.id.editTextCiudad).text.toString()
        val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val ubicacion = findViewById<EditText>(R.id.editTextUbicacion).text.toString()
        val codigoPostal = findViewById<EditText>(R.id.editTextCodigoPostal).text.toString()


        if (documentUri == null || imageUri == null) {
            Toast.makeText(this, "Documento o imagen no seleccionados", Toast.LENGTH_SHORT).show()
            return
        }

        subirArchivoAFirebaseStorage(documentUri, { documentUrl ->
            subirArchivoAFirebaseStorage(imageUri, { imageUrl ->
                val inmueble = Inmueble(alquilado, ciudad, documentUrl, UUID.randomUUID().toString(), imageUrl, nombre, ubicacion, usuarioActual.uid, codigoPostal)
                repository.agregarInmueble(inmueble,
                    onSuccess = {
                        Toast.makeText(this, "Inmueble añadido correctamente", Toast.LENGTH_SHORT).show()
                        limpiarCamposYRedirigir()
                    },
                    onFailure = { e ->
                        Toast.makeText(this, "Error al añadir el inmueble: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }, intentos = 3) // Añade el número de intentos aquí
        }, intentos = 3)
    }
    private fun subirArchivoAFirebaseStorage(uri: Uri?, onUrlObtained: (String) -> Unit, intentos: Int = 3) {
        uri ?: return
        val storageReference = FirebaseStorage.getInstance().reference.child("inmuebles/${UUID.randomUUID()}")
        val uploadTask = storageReference.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                onUrlObtained(downloadUri.toString())
            }
        }.addOnFailureListener { e ->
            if (e is IOException && e.message?.contains("The server has terminated the upload session") == true && intentos > 0) {
                Log.d("AgregarInmuebleActivity", "Reintentando subida... Intentos restantes: ${intentos - 1}")
                subirArchivoAFirebaseStorage(uri, onUrlObtained, intentos - 1)
            } else {
                Toast.makeText(this, getString(R.string.error_al_subir_archivo, e.message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun limpiarCamposYRedirigir() {
        findViewById<EditText>(R.id.editTextAlquilado).setText("")
        findViewById<EditText>(R.id.editTextCiudad).setText("")
        findViewById<EditText>(R.id.editTextNombre).setText("")
        findViewById<EditText>(R.id.editTextUbicacion).setText("")
        val intent = Intent(this, TusInmueblesActivity::class.java)
        startActivity(intent)
        finish()
    }
}