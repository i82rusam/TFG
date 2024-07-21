package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class RegistroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()

        val editTextNombre = findViewById<EditText>(R.id.editTextNombre)
        val editTextApellidos = findViewById<EditText>(R.id.editTextApellidos)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnRegistro = findViewById<Button>(R.id.btn_register)

        btnRegistro.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val nombre = editTextNombre.text.toString().trim()
            val apellidos = editTextApellidos.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellidos.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!esEmailValido(email)) {
                Toast.makeText(this, "El formato del correo electrónico no es válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarUsuario(email, password, nombre, apellidos)
        }
    }

    private fun registrarUsuario(email: String, password: String, nombre: String, apellidos: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    // Asumiendo que tienes EditText para usuario y rol, y que los has inicializado correctamente
                    val usuario = findViewById<EditText>(R.id.et_username).text.toString().trim()
                    val spinnerRole = findViewById<Spinner>(R.id.spinner_role)
                    val rol = spinnerRole.selectedItem.toString()

                    // No se recomienda guardar la contraseña directamente. Considera esta línea como un ejemplo, no como una práctica recomendada.
                    val passwordHash = hashPassword(password) // Necesitarás implementar esta función para hashear la contraseña

                    val userMap = hashMapOf(
                        "nombre" to nombre,
                        "apellidos" to apellidos,
                        "email" to email,
                        "password" to passwordHash, // Almacena "contraseña" a passwordHash
                        "usuario" to usuario,
                        "rol" to rol
                    )
                    FirebaseFirestore.getInstance().collection("users").document(userId)
                        .set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(baseContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Cierra RegistroActivity
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(baseContext, "Error al guardar datos del usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(baseContext, "Registro fallido: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun hashPassword(password: String): String {
        // Implementa la lógica para hashear la contraseña
        return password
    }

    private fun esEmailValido(email: String): Boolean {
        val expresion = "^[A-Za-z0-9+_.-]+@(.+)$"
        val pattern = Pattern.compile(expresion)
        return pattern.matcher(email).matches()
    }
}