package com.example.tfg.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
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
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AgregarInmuebleActivity : AppCompatActivity() {

    private lateinit var repository: FirebaseRepository
    private lateinit var auth: FirebaseAuth

    private val viewModel: InmuebleViewModel by viewModels { InmuebleViewModelFactory(FirebaseRepository(this)) }

    private var documentUri: Uri? = null
    private var imageUri: Uri? = null

    private val documentResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        documentUri = uri
        if (uri != null) {
            Toast.makeText(this, "Documento cargado: $uri", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se seleccionó ningún documento", Toast.LENGTH_SHORT).show()
        }
    }

    private val imageResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        if (uri != null) {
            Toast.makeText(this, "Imagen cargada: $uri", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_inmueble)

        auth = FirebaseAuth.getInstance()
        repository = FirebaseRepository(this)

        val btnCargarDocumento: Button = findViewById(R.id.btnCargarDocumento)
        btnCargarDocumento.setOnClickListener {
            documentResultLauncher.launch("*/*")
        }

        val btnCargarImagen: Button = findViewById(R.id.btnCargarImagen)
        btnCargarImagen.setOnClickListener {
            imageResultLauncher.launch("image/*")
        }

        val btnGuardar: Button = findViewById(R.id.btnGuardar)
        btnGuardar.setOnClickListener {
            guardarInmueble()
        }
    }

    private fun guardarInmueble() {
        val usuarioActual = auth.currentUser
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

        val alquiladoEditText = findViewById<EditText>(R.id.editTextAlquilado)
        val ciudadEditText = findViewById<EditText>(R.id.editTextCiudad)
        val nombreEditText = findViewById<EditText>(R.id.editTextNombre)
        val ubicacionEditText = findViewById<EditText>(R.id.editTextUbicacion)
        val codigoPostalEditText = findViewById<EditText>(R.id.editTextCodigoPostal)

        val alquilado = alquiladoEditText.text.toString().toBoolean()
        val ciudad = ciudadEditText.text.toString()
        val nombre = nombreEditText.text.toString()
        val ubicacion = ubicacionEditText.text.toString()
        val codigoPostal = codigoPostalEditText.text.toString()

        subirArchivo(documentUri, "documentos/${UUID.randomUUID()}",
            onSuccess = { documentoUrl ->
                subirArchivo(imageUri, "imagenes/${UUID.randomUUID()}",
                    onSuccess = { imagenUrl ->
                        val inmueble = Inmueble(alquilado, ciudad, documentoUrl, UUID.randomUUID().toString(), imagenUrl, nombre, ubicacion, usuarioActual.displayName ?: usuarioActual.uid, codigoPostal)
                        repository.agregarInmueble(inmueble, {
                            Toast.makeText(this, "Inmueble añadido correctamente", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, TusInmueblesActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, {
                            Toast.makeText(this, "Error al añadir el inmueble: ${it.message}", Toast.LENGTH_SHORT).show()
                        })
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, "Error al subir la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onFailure = { exception ->
                Toast.makeText(this, "Error al subir el documento: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
    private fun subirArchivo(uri: Uri?, path: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        if (uri == null) {
            onFailure(Exception("El URI del archivo es nulo"))
            return
        }
        val storageRef = FirebaseStorage.getInstance().reference.child(path)
        storageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }?.addOnFailureListener { exception ->
                    onFailure(exception)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    companion object {
        private const val REQUEST_READ_STORAGE = 100
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de lectura concedido", Toast.LENGTH_SHORT).show()
                    documentResultLauncher.launch("*/*")
                } else {
                    Toast.makeText(this, "Permiso de lectura denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
