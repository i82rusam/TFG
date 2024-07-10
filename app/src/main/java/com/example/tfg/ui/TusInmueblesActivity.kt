package com.example.tfg.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.ui.adapter.InmuebleAdapter
import com.google.firebase.auth.FirebaseAuth

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
                repository.eliminarInmueble(inmueble.idInmueble,
                    onSuccess = {
                        // Aquí puedes poner el código que se ejecutará cuando se haya borrado el inmueble con éxito
                        // Por ejemplo, puedes volver a obtener la lista de inmuebles y actualizar el RecyclerView
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null) {
                            repository.getInmuebles(
                                userId, // Pasa el userId como argumento
                                onSuccess = { inmuebles ->
                                    adapter.setInmuebles(inmuebles)
                                },
                                onFailure = { e ->
                                    // Manejar el error
                                    e.printStackTrace()
                                }
                            )
                        } else {
                            Log.d(TAG, "Usuario no autenticado")
                        }
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
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                repository.getInmuebles(
                    userId,
                    onSuccess = { inmuebles ->
                        runOnUiThread {
                            adapter.setInmuebles(inmuebles)
                            adapter.notifyDataSetChanged()
                        }
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Error fetching inmuebles for user $userId", exception)
                    }
                )
            } else {
                Log.d(TAG, "Usuario no autenticado")
            }

    }
}