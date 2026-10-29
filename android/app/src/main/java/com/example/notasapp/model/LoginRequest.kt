package com.example.notasapp.model

/*
 * Modelo que representa los datos necesarios
 * para iniciar sesión en NotasApp.
 *
 * Retrofit utilizará esta clase para enviar
 * el correo y la contraseña al backend.
 */
data class LoginRequest(
    val email: String,
    val password: String
)
