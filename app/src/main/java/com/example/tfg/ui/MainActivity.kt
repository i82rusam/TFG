package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.google.firebase.FirebaseApp
import com.example.tfg.ui.adapter.InmuebleAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: InmuebleAdapter
    private lateinit var repository: FirebaseRepository

    companion object {
        private const val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa adapter aquí antes de llamar a updateInmuebles
        adapter = InmuebleAdapter(emptyList()) { inmueble ->}

        // Inicializa repository aquí
        repository = FirebaseRepository(this)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        // Inicializa el adaptador con una lista vacía y una función de clic vacía
       // adapter = InmuebleAdapter(emptyList()) {}

        //Inicializa Botón Añadir Inmueble
        val btnAddInmueble: Button = findViewById(R.id.btnAddInmueble)
        btnAddInmueble.setOnClickListener { _ ->
            val intent = Intent(this, AgregarInmuebleActivity::class.java)
            startActivity(intent)
        }

        //Inicializa Botón Mis Inmuebles
        val btnMyInmuebles: Button = findViewById(R.id.btnMyInmuebles)
        btnMyInmuebles.setOnClickListener { _->
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
        repository.getInmuebles(onSuccess = { inmuebles ->
            // Actualiza los datos en el adaptador
            adapter.updateData(inmuebles)

            // Notifica al adaptador que los datos han cambiado
            adapter.notifyDataSetChanged()
        }, onFailure = { e ->
            // Maneja el error
            Log.e("MainActivity", "Error al obtener inmuebles", e)
        })
    }
}
