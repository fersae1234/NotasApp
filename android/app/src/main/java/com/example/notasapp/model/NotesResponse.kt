package com.example.notasapp.model

/*
 * Modelo que representa la respuesta del backend
 * cuando consultamos las notas de un usuario.
 *
 * El servidor devuelve un mensaje de confirmación
 * y una lista de notas almacenadas en SQLite.
 *
 * Retrofit y Gson convertirán automáticamente
 * la respuesta JSON en este objeto Kotlin.
 */
data class NotesResponse(

    // Mensaje enviado por el backend.
    val message: String,

    /*
     * Lista de notas del usuario autenticado.
     *
     * Cada elemento utiliza el modelo Note.kt
     * que creamos anteriormente.
     *
     * Si el usuario no tiene notas, la lista
     * estará vacía.
     */
    val notes: List<Note>
)
