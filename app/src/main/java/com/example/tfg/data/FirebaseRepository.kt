package com.example.tfg.data

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.tfg.models.Inmueble
import com.example.tfg.models.Usuario
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class FirebaseRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val inmueblesCollection = firestore.collection("inmuebles")
    private val auth = FirebaseAuth.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()


    fun agregarInmueble(inmueble: Inmueble, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("inmuebles").add(inmueble)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun actualizarInmueble(idInmueble: String, camposActualizados: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("inmuebles").document(idInmueble).update(camposActualizados)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getInmuebles(userId: String, onSuccess: (List<Inmueble>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("inmuebles").whereEqualTo("usuario", userId)
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                val inmuebles = result.toObjects(Inmueble::class.java)
                onSuccess(inmuebles)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun eliminarInmueble(idInmueble: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("inmuebles").document(idInmueble).delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun verificarExistenciaDelInmueble(idInmueble: String, existe: (Boolean) -> Unit, fallo: (Exception) -> Unit) {
        val documentoRef = FirebaseFirestore.getInstance().collection("inmuebles").document(idInmueble)
        documentoRef.get()
            .addOnSuccessListener { documentSnapshot ->
                existe(documentSnapshot.exists())
            }
            .addOnFailureListener { exception ->
                fallo(exception)
            }
    }

    fun verificarYActualizarInmueble(idInmueble: String, camposActualizados: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val documentoRef = FirebaseFirestore.getInstance().collection("inmuebles").document(idInmueble)
        documentoRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                documentoRef.update(camposActualizados)
                    .addOnSuccessListener {
                        Log.d(TAG, "Inmueble actualizado con éxito")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al actualizar inmueble", e)
                        onFailure(e)
                    }
            } else {
                Log.e(TAG, "El inmueble con ID $idInmueble no existe en Firestore.")
                onFailure(Exception("El inmueble con ID $idInmueble no existe en Firestore."))
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error al verificar la existencia del inmueble", exception)
            onFailure(exception)
        }
    }

    fun registrarUsuario(usuario: Usuario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.createUserWithEmailAndPassword(usuario.email, usuario.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    if (userId.isNotEmpty()) {
                        val usuarioFirestore = hashMapOf(
                            "nombre" to usuario.nombre,
                            "apellidos" to usuario.apellidos,
                            "email" to usuario.email
                        )
                        firestore.collection("users").document(userId)
                            .set(usuarioFirestore)
                            .addOnSuccessListener {
                                Log.d(TAG, "Usuario registrado con éxito en Firestore")
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error al registrar usuario en Firestore", e)
                                onFailure(e) // Asegúrate de manejar este error adecuadamente.
                            }
                    } else {
                        Log.e(TAG, "Error obteniendo el ID del usuario")
                        onFailure(Exception("Error obteniendo el ID del usuario"))
                    }
                } else {
                    Log.e(TAG, "Error al registrar al usuario en Auth", task.exception)
                    onFailure(task.exception ?: Exception("Error desconocido al registrar usuario"))
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

    fun getUserData(userId: String, onSuccess: (Usuario) -> Unit, onFailure: (Exception) -> Unit) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    usuario?.let { onSuccess(it) }
                } else {
                    onFailure(Exception("Documento no encontrado"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
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

    fun loadUserData(userId: String, onSuccess: (Map<String, Any?>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    onSuccess(document.data ?: emptyMap())
                } else {
                    onFailure(Exception("No user data found"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun saveUserData(userId: String, userData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun uploadImageToFirebase(fileUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val fileName = UUID.randomUUID().toString()
        val refStorage = firebaseStorage.reference.child("images/$fileName")

        refStorage.putFile(fileUri)
            .addOnSuccessListener {
                refStorage.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun subirArchivoAFirebaseStorage(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val ref = firebaseStorage.reference.child("inmuebles/${UUID.randomUUID()}")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri -> onSuccess(uri.toString()) }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
}