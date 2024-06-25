package com.example.tfg.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tfg.R

class InicioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val btnRegistro = findViewById<Button>(R.id.btn_registro)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        btnRegistro.setOnClickListener {
            // Iniciar la actividad de registro
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            // Iniciar la actividad de inicio de sesión
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }
}