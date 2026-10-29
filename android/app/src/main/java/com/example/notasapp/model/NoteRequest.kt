package com.example.notasapp.model

/*
 * Modelo utilizado para enviar notas al backend.
 *
 * Esta clase contiene la información necesaria
 * para crear o actualizar una nota personal.
 *
 * Retrofit y Gson convierten automáticamente
 * este objeto Kotlin a formato JSON.
 *
 * El backend identifica al usuario mediante
 * el token JWT enviado en la solicitud.
 */
data class NoteRequest(

    // Título de la nota que desea guardar el usuario.
    val title: String,

    // Contenido o descripción de la nota.
    val content: String
)
