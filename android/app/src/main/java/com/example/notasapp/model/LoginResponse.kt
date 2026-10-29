package com.example.notasapp.model

/*
 * Modelo que representa la respuesta del backend
 * cuando un usuario inicia sesión correctamente.
 *
 * Contiene el token de autenticación y los datos
 * básicos del usuario que inició sesión.
 */
data class LoginResponse(
    val token: String,
    val user: User
)

/*
 * Modelo que almacena la información del usuario.
 *
 * El nombre se utilizará posteriormente para
 * mostrar un mensaje de bienvenida en el Dashboard.
 */
data class User(
    val id: Int,
    val name: String,
    val email: String
)
