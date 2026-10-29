package com.example.notasapp.network

import com.example.notasapp.model.LoginRequest
import com.example.notasapp.model.LoginResponse
import com.example.notasapp.model.NotesResponse
import com.example.notasapp.model.NoteRequest
import com.example.notasapp.model.NoteResponse
import com.example.notasapp.model.DeleteNoteResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/*
 * Interfaz que define los endpoints de NotasApp.
 *
 * Retrofit utiliza esta interfaz para realizar
 * solicitudes HTTP a nuestro backend Express.
 *
 * Incluye el inicio de sesión y las operaciones
 * CRUD para administrar las notas personales.
 */
interface ApiService {

    /*
     * LOGIN: Autenticar al usuario.
     *
     * Envía el correo y contraseña al backend.
     * Recibe los datos del usuario y su token JWT.
     */
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    /*
     * READ: Consultar las notas.
     *
     * Recupera todas las notas del usuario
     * autenticado mediante su token JWT.
     *
     * RetrofitClient agrega automáticamente
     * el encabezado Authorization.
     */
    @GET("api/notes")
    suspend fun getNotes(): Response<NotesResponse>

    /*
     * CREATE: Crear una nota.
     *
     * Envía el título y contenido de la nota
     * para almacenarla en SQLite.
     */
    @POST("api/notes")
    suspend fun createNote(
        @Body request: NoteRequest
    ): Response<NoteResponse>

    /*
     * UPDATE: Editar una nota existente.
     *
     * El identificador indica qué nota
     * queremos modificar.
     */
    @PUT("api/notes/{id}")
    suspend fun updateNote(
        @Path("id") id: Int,
        @Body request: NoteRequest
    ): Response<NoteResponse>

    /*
     * DELETE: Eliminar una nota.
     *
     * Solicita al backend eliminar una nota
     * que pertenezca al usuario autenticado.
     */
    @DELETE("api/notes/{id}")
    suspend fun deleteNote(
        @Path("id") id: Int
    ): Response<DeleteNoteResponse>
}
