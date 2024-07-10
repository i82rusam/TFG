package com.example.tfg.data

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.tfg.models.Inmueble
import com.example.tfg.models.Usuario
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val inmueblesCollection = firestore.collection("inmuebles")
    private val auth = FirebaseAuth.getInstance()

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

    fun getInmuebles(userId: String, onSuccess: (List<Inmueble>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("inmuebles")
            .whereEqualTo("usuarioId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val inmuebles = documents.toObjects(Inmueble::class.java)
                Log.d(TAG, "Fetched ${inmuebles.size} inmuebles for user $userId")
                onSuccess(inmuebles)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching inmuebles for user $userId", exception)
                onFailure(exception)
            }
    }

    fun eliminarInmueble(inmuebleId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("inmuebles").document(inmuebleId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun registrarUsuario(usuario: Usuario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(usuario.email, usuario.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // El usuario se registró con éxito

                    // Almacenar la información del usuario
                    firestore.collection("users").document(auth.currentUser!!.uid)
                        .set(usuario)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure(e)
                        }
                } else {
                    // Hubo un error al registrar al usuario
                    onFailure(task.exception!!)
                }
            }
    }

    fun iniciarSesion(usuario: Usuario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.signInWithEmailAndPassword(usuario.email, usuario.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // El usuario inició sesión con éxito
                    onSuccess()
                } else {
                    // Hubo un error al iniciar sesión
                    onFailure(task.exception!!)
                }
            }
    }

    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val email = user.email
            // Ensure email is not null before proceeding
            if (email != null) {
                val credential = EmailAuthProvider.getCredential(email, currentPassword)
                user.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Log.d(TAG, "Password updated")
                                updatePasswordInFirestore(newPassword, onSuccess, onFailure)
                            } else {
                                onFailure(updateTask.exception ?: Exception("Unknown error updating password"))
                            }
                        }
                    } else {
                        onFailure(task.exception ?: Exception("Unknown error reauthenticating"))
                    }
                }
            } else {
                onFailure(Exception("User email is null"))
            }
        } else {
            onFailure(Exception("User is not logged in"))
        }
    }

    fun updatePasswordInFirestore(newPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            firestore.collection("users").document(uid)
                .update("password", newPassword)
                .addOnSuccessListener {
                    Log.d(TAG, "Password updated in Firestore")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error updating password in Firestore", e)
                    onFailure(e)
                }
        } else {
            onFailure(Exception("User is not logged in"))
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

}
