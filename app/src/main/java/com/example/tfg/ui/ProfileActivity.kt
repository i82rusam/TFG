package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseRepository = FirebaseRepository(this)

        val nombreApellidosTextView = findViewById<TextView>(R.id.nombreApellidosTextView)
        val userEmailTextView = findViewById<TextView>(R.id.emailTextView)
        val user = FirebaseAuth.getInstance().currentUser
        userEmailTextView.text = user?.email ?: "Correo no disponible"

        user?.uid?.let { userId ->
            firebaseRepository.getUserData(userId, { usuario ->
                // onSuccess
                nombreApellidosTextView.text = getString(R.string.nombre_apellidos, usuario.nombre, usuario.apellidos)
            }, { exception ->
                // onFailure
                Toast.makeText(this, "Failed to load user data: ${exception.message}", Toast.LENGTH_SHORT).show()
            })
        }

        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword)
        val btnSignOut = findViewById<Button>(R.id.btnSignOut)
        val btnEditUser = findViewById<Button>(R.id.btn_edit_user)

        btnEditUser.setOnClickListener {
            // Inicia EditUserActivity cuando se hace clic en el botón
            val intent = Intent(this, EditUserActivity::class.java)
            startActivity(intent)
        }

        btnChangePassword.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
            val currentPasswordEditText = dialogView.findViewById<EditText>(R.id.currentPasswordEditText)
            val newPasswordEditText = dialogView.findViewById<EditText>(R.id.newPasswordEditText)

            AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Change Password")
                .setPositiveButton("Change") { dialog, which ->
                    val currentPassword = currentPasswordEditText.text.toString().trim()
                    val newPassword = newPasswordEditText.text.toString().trim()

                    if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                        Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show()
                    } else {
                        firebaseRepository.changePassword(currentPassword, newPassword, {
                            // onSuccess
                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        }, { exception ->
                            // onFailure
                            Toast.makeText(this, "Failed to change password: ${exception.message}", Toast.LENGTH_SHORT).show()
                        })
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnSignOut.setOnClickListener {
            firebaseRepository.signOut()
            // Redirigir al usuario a la pantalla de inicio de sesión
            val intent = Intent(this, InicioActivity::class.java)
            // Limpiar la pila de actividades para evitar que el usuario regrese a la pantalla de perfil después de cerrar sesión
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData() {
        try {
            val user = FirebaseAuth.getInstance().currentUser
            user?.uid?.let { userId ->
                FirebaseFirestore.getInstance().collection("users").document(userId)
                    .get(Source.SERVER)
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val nombre = document.getString("name") ?: "Nombre no disponible"
                            val apellidos = document.getString("lastName") ?: "Apellido no disponible"
                            val profileImageUrl = document.getString("profileImageUrl")
                            findViewById<TextView>(R.id.nombreApellidosTextView).text = getString(R.string.nombre_apellidos, nombre, apellidos)
                            if (!profileImageUrl.isNullOrEmpty()) {
                                Glide.with(applicationContext)
                                    .load(profileImageUrl)
                                    .error(R.drawable.ic_profile)
                                    .into(findViewById(R.id.ivProfilePicture))
                            } else {
                                Glide.with(applicationContext)
                                    .load(R.drawable.ic_profile)
                                    .into(findViewById(R.id.ivProfilePicture))
                            }
                        } else {
                            Toast.makeText(this, "Documento no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al cargar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}