package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val buttonLogin = findViewById<Button>(R.id.buttonRegister) // Asegúrate de que el ID corresponda al botón de inicio de sesión
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail) // Asegúrate de que el ID corresponda al campo de correo electrónico
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Inicio de sesión exitoso
                        Log.d("LoginActivity", "Inicio de sesión exitoso")
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                        // Verificar si currentUser no es null
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            Log.d("LoginActivity", "Usuario actual: ${currentUser.email}")
                            // Redirigir al usuario a MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Cierra LoginActivity
                        } else {
                            Log.d("LoginActivity", "currentUser es null después del inicio de sesión")
                            Toast.makeText(this, "Error de autenticación post-inicio de sesión", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Inicio de sesión fallido, manejar el error
                        Log.w("LoginActivity", "Error al iniciar sesión", task.exception)
                        Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
