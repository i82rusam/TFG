package com.example.tfg

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_inmueble)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        val editTextCiudad = findViewById<EditText>(R.id.editTextCiudad)

        editTextCiudad.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus) {
                val ciudadIngresada = editTextCiudad.text.toString()

               /* // Validar la entrada del usuario
                if (ciudadIngresada.isNotBlank()) {
                    // El usuario ha ingresado una ciudad válida, puedes realizar alguna acción basada en ella

                } else {
                    // El usuario no ha ingresado una ciudad válida, muestra un mensaje de error o realiza alguna acción apropiada
                    mostrarMensajeError("Por favor, ingresa una ciudad válida")
                }*/
            }
        }
    }

   /* private fun mostrarCiudad(ciudad: String) {
    }
    private fun mostrarMensajeError(mensaje: String) {
        // Aquí puedes mostrar un mensaje de error al usuario, por ejemplo, usando un Toast o un AlertDialog
    }


    */
}