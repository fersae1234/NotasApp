package com.example.notasapp.model

/*
 * Modelo de datos que representa una nota.
 *
 * Esta clase permite convertir la información
 * JSON recibida desde nuestro backend
 * en un objeto Kotlin.
 *
 * Cada nota pertenece a un usuario y contiene
 * un identificador, título, contenido y fechas.
 */
data class Note(

    // Identificador único de la nota en SQLite.
    val id: Int,

    // Título de la nota.
    val title: String,

    // Contenido o descripción de la nota.
    val content: String,

    // Fecha en la que se creó la nota.
    val created_at: String,

    // Fecha de la última modificación.
    val updated_at: String
)
