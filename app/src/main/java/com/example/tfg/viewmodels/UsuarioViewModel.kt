package com.example.tfg.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Usuario

class UsuarioViewModel(private val repository: FirebaseRepository) : ViewModel() {

    fun iniciarSesion(email: String, password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val usuario = Usuario(email, password)
        repository.iniciarSesion(usuario, onSuccess, onFailure)
    }

   // fun cerrarSesion() {
     //   repository.cerrarSesion()
    //}

    fun registrarUsuario(usuario: Usuario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repository.registrarUsuario(usuario, onSuccess, onFailure)
    }
}
