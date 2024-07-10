package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseRepository = FirebaseRepository(this)

            val userEmailTextView = findViewById<TextView>(R.id.emailTextView)
            val user = FirebaseAuth.getInstance().currentUser
            userEmailTextView.text = user?.email ?: "Correo no disponible"


        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword)
        val btnSignOut = findViewById<Button>(R.id.btnSignOut)

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

        btnSignOut.setOnClickListener{
            firebaseRepository.signOut()
            // Redirigir al usuario a la pantalla de inicio de sesión
            val intent = Intent(this, InicioActivity::class.java)
            // Limpiar la pila de actividades para evitar que el usuario regrese a la pantalla de perfil después de cerrar sesión
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
}
}