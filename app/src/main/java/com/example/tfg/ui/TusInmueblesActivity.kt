package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble
import com.example.tfg.ui.adapter.InmuebleAdapter
import com.google.firebase.auth.FirebaseAuth

class TusInmueblesActivity : AppCompatActivity() {

    private lateinit var adapter: InmuebleAdapter
    private lateinit var repository: FirebaseRepository
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "TusInmueblesActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tus_inmuebles)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewInmuebles)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = InmuebleAdapter(
            emptyList(),
            itemClick = { inmueble: Inmueble ->
                val intent = Intent(this, InmuebleDetailActivity::class.java)
                intent.putExtra(InmuebleDetailActivity.EXTRA_INMUEBLE_ID, inmueble.idInmueble)
                startActivity(intent)
            },
            itemEdit = { inmueble: Inmueble ->
                val intent = Intent(this, EditarInmuebleActivity::class.java)
                intent.putExtra("INMUEBLE_ID", inmueble.idInmueble)
                startActivity(intent)
            },
            itemDelete = { inmueble: Inmueble ->
                repository.eliminarInmueble(inmueble.idInmueble, onSuccess = {
                    Log.d(TAG, "Inmueble eliminado con éxito en Firebase")
                }, onFailure = { e ->
                    Log.e(TAG, "Error al eliminar el inmueble en Firebase", e)
                    e.printStackTrace()
                })
            }
        )
        recyclerView.adapter = adapter

        repository = FirebaseRepository(this)
    }

    override fun onResume() {
        super.onResume()
        updateInmuebles()
    }

    private fun updateInmuebles() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            Log.d(TAG, "Usuario autenticado con ID: $userId")
            repository.getInmuebles(
                userId,
                onSuccess = { inmuebles: List<Inmueble> ->
                    Log.d(TAG, "Inmuebles obtenidos: ${inmuebles.size}")
                    adapter.setInmuebles(inmuebles)
                    adapter.notifyDataSetChanged()
                },
                onFailure = { e: Exception ->
                    Log.e(TAG, "Error al obtener inmuebles: ", e)
                }
            )
        } else {
            Log.d(TAG, "Usuario no autenticado")
        }
    }
}