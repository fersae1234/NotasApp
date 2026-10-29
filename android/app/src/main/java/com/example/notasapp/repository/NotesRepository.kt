package com.example.notasapp.repository

import com.example.notasapp.model.DeleteNoteResponse
import com.example.notasapp.model.NoteRequest
import com.example.notasapp.model.NoteResponse
import com.example.notasapp.model.NotesResponse
import com.example.notasapp.network.ApiService
import retrofit2.Response

/*
 * Repositorio encargado de administrar las notas.
 *
 * Esta clase conecta la lógica de la aplicación
 * con los endpoints de nuestro backend Express.
 *
 * Permite consultar, crear, actualizar y eliminar
 * las notas del usuario autenticado.
 *
 * Todas las funciones son suspend para realizar
 * solicitudes de red mediante corrutinas de Kotlin.
 */
class NotesRepository(
    private val apiService: ApiService
) {

    /*
     * READ: Obtener todas las notas del usuario.
     *
     * El backend utiliza el token JWT para
     * identificar al usuario autenticado.
     */
    suspend fun getNotes(): Response<NotesResponse> {
        return apiService.getNotes()
    }

    /*
     * CREATE: Crear una nota nueva.
     *
     * Recibe el título y contenido, construye
     * la solicitud y la envía al servidor.
     */
    suspend fun createNote(
        title: String,
        content: String
    ): Response<NoteResponse> {

        val request = NoteRequest(
            title = title,
            content = content
        )

        return apiService.createNote(request)
    }

    /*
     * UPDATE: Actualizar una nota existente.
     *
     * Utiliza el identificador de la nota
     * para modificar su título y contenido.
     */
    suspend fun updateNote(
        id: Int,
        title: String,
        content: String
    ): Response<NoteResponse> {

        val request = NoteRequest(
            title = title,
            content = content
        )

        return apiService.updateNote(id, request)
    }

    /*
     * DELETE: Eliminar una nota.
     *
     * Envía al servidor el identificador
     * de la nota que queremos eliminar.
     */
    suspend fun deleteNote(
        id: Int
    ): Response<DeleteNoteResponse> {
        return apiService.deleteNote(id)
    }
}
