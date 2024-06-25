package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Usuario

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val spinnerRole = findViewById<Spinner>(R.id.spinner_role)
        val btnRegister = findViewById<Button>(R.id.btn_register)

        val firebaseRepository = FirebaseRepository(this)
        btnRegister.setOnClickListener {
            val usuario = Usuario().apply {
                username = etUsername.text.toString()
                password = etPassword.text.toString()
                role = spinnerRole.selectedItem.toString()
            }

            firebaseRepository.registrarUsuario(usuario,
                onSuccess = {
                    // El usuario se registró con éxito
                    Log.d("RegistroActivity", "Usuario registrado con éxito en Firebase")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Para que no se pueda volver a RegistroActivity al presionar el botón atrás
                },
                onFailure = { e ->
                    // Hubo un error al registrar al usuario
                    Log.e("RegistroActivity", "Error al registrar al usaurio en Firebase", e)
                    e.printStackTrace()
                }
            )
        }
    }
}