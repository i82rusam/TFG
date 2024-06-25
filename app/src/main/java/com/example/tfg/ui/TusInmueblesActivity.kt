package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.ui.adapter.InmuebleAdapter

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

        // Inicializar el adaptador con una lista vacía
        adapter = InmuebleAdapter(emptyList(),
            itemClick = { inmueble ->
                val intent = Intent(this, InmuebleDetailActivity::class.java)
                intent.putExtra("inmueble", inmueble)
                startActivity(intent)
            },
            itemDelete = { inmueble ->
                repository.deleteInmueble(inmueble.idInmueble,
                    onSuccess = {
                        // Aquí puedes poner el código que se ejecutará cuando se haya borrado el inmueble con éxito
                        // Por ejemplo, puedes volver a obtener la lista de inmuebles y actualizar el RecyclerView
                        repository.getInmuebles(
                            onSuccess = { inmuebles ->
                                adapter.setInmuebles(inmuebles)
                            },
                            onFailure = { e ->
                                // Manejar el error
                                e.printStackTrace()
                            }
                        )
                    },
                    onFailure = { e ->
                        // Manejar el error
                        e.printStackTrace()
                    }
                )
            }
        )
        recyclerView.adapter = adapter
        // Inicializar el repositorio de Firebase
        repository = FirebaseRepository(this)

        updateData()
    }
    override fun onResume() {
        super.onResume()

        // Actualiza los datos cada vez que la actividad se reanuda
        updateData()
    }

    private fun updateData() {
        // Obtén los datos actualizados de Firebase
        repository.getInmuebles(
            onSuccess = { inmuebles ->
                // Actualiza los datos en el adaptador
                adapter.setInmuebles(inmuebles)
            },
            onFailure = { e ->
                // Maneja el error
                e.printStackTrace()
            }
        )
    }
}