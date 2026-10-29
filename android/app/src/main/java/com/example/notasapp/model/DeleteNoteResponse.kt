package com.example.notasapp.model

/*
 * Modelo que representa la respuesta del backend
 * cuando eliminamos una nota.
 *
 * El servidor devuelve un mensaje indicando
 * si la operación se realizó correctamente.
 *
 * Retrofit y Gson convierten automáticamente
 * la respuesta JSON en este objeto Kotlin.
 */
data class DeleteNoteResponse(

    /*
     * Mensaje de confirmación enviado
     * por nuestro backend Express.
     *
     * Ejemplo:
     * "Nota eliminada correctamente"
     */
    val message: String
)
