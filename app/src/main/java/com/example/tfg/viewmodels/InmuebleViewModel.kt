package com.example.tfg.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tfg.data.FirebaseRepository
import com.example.tfg.models.Inmueble

class InmuebleViewModel(private val repository: FirebaseRepository) : ViewModel() {

    fun agregarInmueble(inmueble: Inmueble, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repository.agregarInmueble(inmueble, onSuccess, onFailure)
    }

    fun obtenerInmuebles(userId: String, onSuccess: (List<Inmueble>) -> Unit, onFailure: (Exception) -> Unit) {
        repository.getInmuebles(userId, onSuccess, onFailure)
    }

    fun actualizarInmueble(idInmueble: String, inmueble: Inmueble, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repository.actualizarInmueble(idInmueble, inmueble, onSuccess, onFailure)
    }

    fun eliminarInmueble(idInmueble: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repository.eliminarInmueble(idInmueble, onSuccess, onFailure)
    }
}

class InmuebleViewModelFactory(private val repository: FirebaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InmuebleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InmuebleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}