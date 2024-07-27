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

    private lateinit var auth: FirebaseAuth
    private lateinit var repository: FirebaseRepository
    private lateinit var adapter: InmuebleAdapter

    companion object {
        private const val TAG = "TusInmueblesActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tus_inmuebles)

        auth = FirebaseAuth.getInstance()
        repository = FirebaseRepository(this)

        if (auth.currentUser == null) {
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        updateInmuebles()
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewInmuebles)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = InmuebleAdapter(
            inmuebles = listOf(),
            itemClick = { inmueble ->
                val intent = Intent(this, InmuebleDetailActivity::class.java)
                intent.putExtra(InmuebleDetailActivity.EXTRA_INMUEBLE_ID, inmueble.idInmueble)
                Log.d(TAG, "Passing idInmueble: ${inmueble.idInmueble} to InmuebleDetailActivity")
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
    }

    private fun updateInmuebles() {
        Log.d(TAG, "updateInmuebles called")
        val usuario = auth.currentUser?.uid
        if (usuario != null) {
            Log.d(TAG, "Usuario autenticado con ID: $usuario")
            repository.getInmuebles(
                usuario,
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
        Log.d(TAG, "updateInmuebles finished")
    }
}