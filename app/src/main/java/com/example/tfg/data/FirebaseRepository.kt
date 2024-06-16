package com.example.tfg.data

import android.content.Context
import com.example.tfg.models.Inmueble
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val inmueblesCollection = firestore.collection("inmuebles")
    val auth = FirebaseAuth.getInstance()

    fun agregarInmueble(inmueble: Inmueble, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        inmueblesCollection.add(inmueble).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

    fun actualizarInmueble(inmueble: Inmueble, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        inmueblesCollection.document(inmueble.idInmueble).set(inmueble)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun getInmuebles(onSuccess: (List<Inmueble>) -> Unit, onFailure: (Exception) -> Unit) {
        inmueblesCollection.get().addOnSuccessListener { result ->
            val inmuebles = result.toObjects(Inmueble::class.java)
            onSuccess(inmuebles)
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }
    fun deleteInmueble(inmuebleId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("inmuebles").document(inmuebleId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }


}
