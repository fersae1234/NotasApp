package com.example.notasapp.model

/*
 * Modelo que representa la respuesta del backend
 * cuando creamos o actualizamos una nota.
 *
 * El servidor devuelve un mensaje de confirmación
 * y la información de la nota almacenada en SQLite.
 *
 * Retrofit y Gson convierten automáticamente
 * la respuesta JSON en este objeto Kotlin.
 */
data class NoteResponse(

    // Mensaje de confirmación enviado por el servidor.
    val message: String,

    /*
     * Nota creada o actualizada.
     *
     * Utilizamos el modelo Note.kt
     * que desarrollamos anteriormente.
     */
    val note: Note
)
