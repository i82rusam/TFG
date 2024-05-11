package com.example.tfg.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.core.view.View

class AgregarInmuebleActivity : AppCompatActivity() {

    private lateinit var repository: FirebaseRepository
    private lateinit var auth: FirebaseAuth
    private var idAleatorio: Int = 0

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


    companion object {
        private const val REQUEST_READ_STORAGE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_inmueble)
        Log.d("agregarInmuebleActivity", "Función botón llamada1")

        repository = FirebaseRepository()
        auth = FirebaseAuth.getInstance()
    }

    fun cargarDocumento(@SuppressLint("RestrictedApi") view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            documentResultLauncher.launch("*/*")
        } else {
            Toast.makeText(this, "Permiso de lectura no concedido", Toast.LENGTH_SHORT).show()
        }
    }

    fun guardarInmueble() {
        Log.d("AgregarInmuebleActivity", "Función guardarInmueble llamada")

        val alquilado = findViewById<EditText>(R.id.editTextAlquilado).text.toString().toIntOrNull() ?: 0
        val ciudad = findViewById<EditText>(R.id.editTextCiudad).text.toString()
        val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val ubicacion = findViewById<EditText>(R.id.editTextUbicacion).text.toString()

        Log.d("AgregarInmuebleActivity", "Datos recogidos: alquilado=$alquilado, ciudad=$ciudad, nombre=$nombre, ubicacion=$ubicacion")

        val usuarioActual = auth.currentUser?.displayName ?: "Nombre de usuario predeterminado"

        val documentUriString = documentUri?.toString() ?: ""
        val imageUriString = imageUri?.toString() ?: ""

        val inmueble = Inmueble(alquilado, ciudad, documentUri.toString(), idAleatorio.toString(), imageUri.toString(), nombre, ubicacion, usuarioActual)

        Log.d("AgregarInmuebleActivity", "Inmueble creado: $inmueble")

        repository.agregarInmueble(inmueble,
            onSuccess = {
                Toast.makeText(this, "Inmueble añadido correctamente", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Toast.makeText(this, "Error al añadir el inmueble: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
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