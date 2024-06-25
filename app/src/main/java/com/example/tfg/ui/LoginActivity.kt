package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Usuario

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        firebaseRepository = FirebaseRepository(this)

        btnLogin.setOnClickListener {
            val usuario = Usuario().apply {
                username = etUsername.text.toString()
                password = etPassword.text.toString()
            }

            firebaseRepository.iniciarSesion(usuario,
                onSuccess = {
                    // El usuario inició sesión con éxito
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailure = { _ ->
                    // Hubo un error al iniciar sesión
                }
            )
        }
    }
}