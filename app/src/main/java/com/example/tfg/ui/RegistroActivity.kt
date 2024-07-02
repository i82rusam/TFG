package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Usuario
import java.util.regex.Pattern

class RegistroActivity : AppCompatActivity() {

    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        firebaseRepository = FirebaseRepository(this)

        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnRegistro = findViewById<Button>(R.id.btn_register)

        btnRegistro.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (!esEmailValido(email)) {
                Toast.makeText(this, "El formato del correo electrónico no es válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = Usuario(email = email, password = password)
            firebaseRepository.registrarUsuario(usuario, onSuccess = {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, onFailure = { exception ->
                Toast.makeText(baseContext, "Registro fallido: ${exception.message}", Toast.LENGTH_SHORT).show()
            })
        }
    }

    fun esEmailValido(email: String): Boolean {
        val expresion = "^[\\w.-]+@([\\w-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expresion, Pattern.CASE_INSENSITIVE)
        return pattern.matcher(email).matches()
    }
}