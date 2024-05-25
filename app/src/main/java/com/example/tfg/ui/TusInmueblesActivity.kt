package com.example.tfg.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository

class TusInmueblesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InmuebleAdapter
    private lateinit var repository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tus_inmuebles)

        // Configurar el Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewInmuebles)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializar el repositorio de Firebase
        repository = FirebaseRepository(this)

        // Configurar el Adapter
        adapter = InmuebleAdapter(emptyList())
        recyclerView.adapter = adapter

        // Cargar los inmuebles desde Firebase
        cargarInmuebles()
    }

    private fun cargarInmuebles() {
        repository.getInmuebles(
            onSuccess = { inmuebles ->
                adapter.setInmuebles(inmuebles)
            },
            onFailure = { e ->
                // Manejar el error
                e.printStackTrace()
            }
        )
    }
}
