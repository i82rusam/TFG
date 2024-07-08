package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.ui.adapter.InmuebleAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: InmuebleAdapter
    private lateinit var repository: FirebaseRepository
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa Firebase y la autenticación
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Verificar si el usuario está autenticado
        if (auth.currentUser == null) {
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Inicializa adapter aquí antes de llamar a updateInmuebles
        adapter = InmuebleAdapter(
            emptyList(),
            itemClick = { inmueble ->
                val intent = Intent(this, InmuebleDetailActivity::class.java)
                intent.putExtra(InmuebleDetailActivity.EXTRA_INMUEBLE, inmueble)
                startActivity(intent)
            },
            itemDelete = { inmueble ->
                repository.eliminarInmueble(inmueble.idInmueble, onSuccess = {
                    Log.d("MainActivity", "Inmueble eliminado con éxito en Firebase")
                }, onFailure = { e ->
                    Log.e("MainActivity", "Error al eliminar el inmueble en Firebase", e)
                    e.printStackTrace()
                })
            }
        )

        // Inicializa repository aquí
        repository = FirebaseRepository(this)

        // Inicializa Botón Añadir Inmueble
        val btnAddInmueble: Button = findViewById(R.id.btnAddInmueble)
        btnAddInmueble.setOnClickListener {
            Log.d("MainActivity", "Intentando abrir AgregarInmuebleActivity")
            Toast.makeText(this, "Intentando abrir AgregarInmuebleActivity", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AgregarInmuebleActivity::class.java)
            startActivity(intent)
        }

        // Inicializa Botón Mis Inmuebles
        val btnMyInmuebles: Button = findViewById(R.id.btnMyInmuebles)
        btnMyInmuebles.setOnClickListener {
            val intent = Intent(this, TusInmueblesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualiza la lista de inmuebles cada vez que la actividad se reanuda
        updateInmuebles()
    }

    private fun updateInmuebles() {
        // Obtén los datos actualizados de Firebase
        val userId = auth.currentUser?.uid
        if (userId != null) {
            repository.getInmuebles(
                userId,
                onSuccess = { inmuebles ->
                    adapter.setInmuebles(inmuebles)
                    // Asegúrate de notificar al adaptador que los datos han cambiado
                    adapter.notifyDataSetChanged()
                },
                onFailure = { e ->
                    Log.e(TAG, "Error al obtener inmuebles: ", e)
                }
            )
        } else {
            Log.d(TAG, "Usuario no autenticado")
        }
    }
}
