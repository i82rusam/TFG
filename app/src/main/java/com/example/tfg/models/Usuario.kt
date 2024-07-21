package com.example.tfg.models

data class Usuario(
    var email: String = "",
    var password: String = "",
    var role: String = "",
    var nombre: String = "",
    var apellidos: String = ""
)