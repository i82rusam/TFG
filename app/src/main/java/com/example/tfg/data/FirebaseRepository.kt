package com.example.tfg.data
import android.content.Context
import android.util.Log
import com.example.tfg.models.Inmueble
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FirebaseRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val inmueblesCollection = firestore.collection("inmuebles")
    val auth = FirebaseAuth.getInstance()

    fun agregarInmueble(inmueble: Inmueble, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("FirebaseRepository", "Agregando inmueble: $inmueble")
        inmueblesCollection.add(inmueble).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

    fun listarInmuebles(): LiveData<List<Inmueble>> {
        return inmuebleDao.listarInmuebles()
    }
}