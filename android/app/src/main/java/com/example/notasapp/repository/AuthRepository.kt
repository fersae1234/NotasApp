package com.example.notasapp.repository

import com.example.notasapp.model.LoginRequest
import com.example.notasapp.model.LoginResponse
import com.example.notasapp.network.ApiService
import com.example.notasapp.network.TokenManager
import retrofit2.Response

/*
 * Repositorio encargado de gestionar la autenticación.
 *
 * Forma parte de la arquitectura MVVM y funciona
 * como intermediario entre el ViewModel y el backend.
 *
 * Utiliza ApiService para realizar las peticiones
 * y TokenManager para guardar el token recibido.
 */
class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    /*
     * Función que realiza el inicio de sesión.
     *
     * Recibe el correo y la contraseña del usuario,
     * crea un LoginRequest y envía los datos al backend.
     *
     * Si la respuesta es exitosa, guarda el token
     * para utilizarlo en futuras peticiones.
     *
     * suspend permite ejecutar la operación
     * mediante corrutinas.
     */
    suspend fun login(
        email: String,
        password: String
    ): Response<LoginResponse> {

        // Prepara los datos que se enviarán al servidor.
        val request = LoginRequest(
            email = email,
            password = password
        )

        // Realiza la petición HTTP mediante Retrofit.
        val response = apiService.login(request)

        // Comprueba que el servidor respondió correctamente.
        if (response.isSuccessful) {

            // Obtiene el token de la respuesta.
            val token = response.body()?.token

            // Guarda el token si existe y no está vacío.
            if (!token.isNullOrEmpty()) {
                tokenManager.saveToken(token)
            }
        }

        // Devuelve la respuesta para que el ViewModel la procese.
        return response
    }

    /*
     * Función para cerrar sesión.
     *
     * Elimina el token almacenado para impedir
     * que se siga utilizando en nuevas peticiones.
     */
    fun logout() {
        tokenManager.clearToken()
    }

    /*
     * Comprueba si existe un token guardado.
     *
     * Se utilizará para determinar si el usuario
     * tiene una sesión almacenada.
     *
     * La existencia del token no garantiza
     * que todavía sea válido en el servidor.
     */
    fun hasSession(): Boolean {
        return !tokenManager.getToken().isNullOrEmpty()
    }
}
