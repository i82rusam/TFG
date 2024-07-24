package com.example.tfg.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
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
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class EditarInmuebleActivity : AppCompatActivity() {

    private lateinit var repository: FirebaseRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var inmueble: Inmueble
    private lateinit var imageResultLauncher: ActivityResultLauncher<String>


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

        imageResultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Paso 3: Manejar el resultado de la selección de la imagen
            uri?.let {
                // Aquí puedes usar la URI de la imagen, por ejemplo, mostrarla en un ImageView
            }
        }

        // Corrección del identificador para coincidir con el usado al enviar el objeto desde InmuebleDetailActivity
        val inmuebleIntent = intent.getParcelableExtra<Inmueble>(InmuebleDetailActivity.EXTRA_INMUEBLE) ?: run {
            Toast.makeText(this, "Error: Inmueble no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        inmueble = inmuebleIntent

        val editTextNombre: EditText = findViewById(R.id.editTextNombre)
        val editTextCiudad: EditText = findViewById(R.id.editTextCiudad)
        val editTextUbicacion: EditText = findViewById(R.id.editTextUbicacion)
        val editTextCodigoPostal: EditText = findViewById(R.id.editTextCodigoPostal)
        val buttonCargarDocumento: Button = findViewById(R.id.btnCargarDocumento)
        val buttonCargarImagen: Button = findViewById(R.id.btnCargarImagen)
        val buttonActualizarInmueble: Button = findViewById(R.id.boton_guardar)

        editTextNombre.setText(inmueble.nombre)
        editTextCiudad.setText(inmueble.ciudad)
        editTextUbicacion.setText(inmueble.ubicacion)
        editTextCodigoPostal.setText(inmueble.codigoPostal)


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

        val btnCargarImagen = findViewById<Button>(R.id.btnCargarImagen)
        btnCargarImagen.setOnClickListener {
            // Lanzar la galería
            imageResultLauncher.launch("image/*")
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
        val codigoPostal = findViewById<EditText>(R.id.editTextCodigoPostal).text.toString()
        val alquilado = findViewById<Switch>(R.id.switchAlquilado).isChecked

        // Solo agregar al mapa los campos que han sido modificados
        if (nombre.isNotEmpty()) camposActualizados["nombre"] = nombre
        if (ciudad.isNotEmpty()) camposActualizados["ciudad"] = ciudad
        if (ubicacion.isNotEmpty()) camposActualizados["ubicacion"] = ubicacion
        if (codigoPostal.isNotEmpty()) camposActualizados["codigoPostal"] = codigoPostal
        camposActualizados["alquilado"] = alquilado // Asegúrate de que este campo se maneje como booleano

        // Subir y actualizar la URI del documento si existe
        documentUri?.let {
            subirArchivo(it, "documentos/${UUID.randomUUID()}",
                onSuccess = { documentoUrl ->
                    camposActualizados["escritura"] = documentoUrl
                    // Subir y actualizar la URI de la imagen si existe
                    imageUri?.let { uri ->
                        subirArchivo(uri, "imagenes/${UUID.randomUUID()}",
                            onSuccess = { imagenUrl ->
                                camposActualizados["imagen"] = imagenUrl
                                realizarActualizacionConVerificacion(camposActualizados)
                            },
                            onFailure = { exception ->
                                Toast.makeText(this, "Error al subir la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } ?: realizarActualizacionConVerificacion(camposActualizados) // Si no hay imagen nueva, actualizar solo con documento
                },
                onFailure = { exception ->
                    Toast.makeText(this, "Error al subir el documento: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } ?: imageUri?.let { // Si no hay documento nuevo pero sí imagen nueva
            subirArchivo(it, "imagenes/${UUID.randomUUID()}",
                onSuccess = { imagenUrl ->
                    camposActualizados["imagen"] = imagenUrl
                    realizarActualizacionConVerificacion(camposActualizados)
                },
                onFailure = { exception ->
                    Toast.makeText(this, "Error al subir la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } ?: if (camposActualizados.isNotEmpty()) realizarActualizacionConVerificacion(camposActualizados) // Si no hay archivos nuevos pero sí otros campos actualizados
        else Toast.makeText(this, "No se han realizado cambios.", Toast.LENGTH_SHORT).show()
    }

    private fun realizarActualizacionConVerificacion(camposActualizados: HashMap<String, Any>) {
        inmueble.idInmueble?.let { idInmueble ->
            repository.verificarYActualizarInmueble(idInmueble, camposActualizados, {
                // Éxito: El inmueble se ha actualizado correctamente
                Toast.makeText(this, "Inmueble actualizado con éxito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, TusInmueblesActivity::class.java)
                startActivity(intent)
                finish()
            }, { exception ->
                // Error: No se pudo actualizar el inmueble
                Toast.makeText(this, "Error al actualizar el inmueble: ${exception.message}", Toast.LENGTH_SHORT).show()
            })
        } ?: Toast.makeText(this, "ID del inmueble no encontrado", Toast.LENGTH_SHORT).show()
    }

    private fun subirArchivo(uri: Uri, path: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child(path)
        storageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }?.addOnFailureListener { exception ->
                    Log.e("EditarInmuebleActivity", "Error al obtener URL de descarga", exception)
                    onFailure(exception)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("EditarInmuebleActivity", "Error al subir archivo a Firebase Storage", exception)
                onFailure(exception)
            }
    }
}