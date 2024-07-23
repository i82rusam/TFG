package com.example.tfg.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseRepository = FirebaseRepository(this)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        val userEmailTextView = findViewById<TextView>(R.id.emailTextView)
        val user = auth.currentUser
        userEmailTextView.text = user?.email ?: "Correo no disponible"

        loadUserData()

        val btnSignOut = findViewById<Button>(R.id.btnSignOut)
        val btnEditUser = findViewById<Button>(R.id.btn_edit_user)

        btnEditUser.setOnClickListener {
            startActivity(Intent(this, EditUserActivity::class.java))
        }

        btnSignOut.setOnClickListener {
            signOut()
        }
    }
    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData() {
        val user = auth.currentUser
        user?.let {
            val profileImageView = findViewById<ImageView>(R.id.profileImageView)
            val userNameTextView = findViewById<TextView>(R.id.nombreApellidosTextView)
            val emailTextView = findViewById<TextView>(R.id.emailTextView)

            // Actualizar el TextView del correo directamente desde el usuario de FirebaseAuth
            emailTextView.text = it.email ?: "Correo no disponible"

            // Obtener el nombre, apellidos y URL de la imagen de perfil desde Firestore
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("users").document(it.uid)
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val firstName = document.getString("name") ?: "Nombre no disponible"
                    val lastName = document.getString("lastName") ?: "Apellidos no disponibles"
                    userNameTextView.text = "$firstName $lastName"
                    // Obtener la URL de la imagen de perfil
                    val profileImageUrl = document.getString("profileImageUrl")
                    profileImageUrl?.let { url ->
                        loadProfileImage(Uri.parse(url), profileImageView)
                    } ?: profileImageView.setImageResource(R.drawable.ic_profile)
                } else {
                    userNameTextView.text = "Documento no encontrado"
                    profileImageView.setImageResource(R.drawable.ic_profile)
                }
            }.addOnFailureListener { exception ->
                userNameTextView.text = "Error al obtener datos"
                profileImageView.setImageResource(R.drawable.ic_profile)
            }
        } ?: Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
    }
    private fun loadProfileImage(uri: Uri, imageView: ImageView) {
        Glide.with(this).load(uri).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(imageView)
    }

    private fun signOut() {
        // 1. Desconectar al usuario
        FirebaseAuth.getInstance().signOut()

        // 2. Redirigir al usuario a LoginActivity
        val intent = Intent(this, InicioActivity::class.java)

        // 3. Limpiar la pila de actividades
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}