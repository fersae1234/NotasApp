package com.example.notasapp.network

import android.content.Context

/*
 * Clase encargada de administrar el token
 * de autenticación del usuario.
 *
 * Utiliza SharedPreferences para conservar
 * el token entre ejecuciones de la aplicación.
 */
class TokenManager(context: Context) {

    /*
     * Archivo de preferencias donde se almacenará
     * temporalmente la información de autenticación.
     */
    private val preferences = context.getSharedPreferences(
        "NotasAppPrefs",
        Context.MODE_PRIVATE
    )

    /*
     * Guarda el token recibido del backend
     * después de iniciar sesión correctamente.
     */
    fun saveToken(token: String) {
        preferences.edit()
            .putString("auth_token", token)
            .apply()
    }

    /*
     * Recupera el token almacenado.
     * Si no existe, devuelve null.
     */
    fun getToken(): String? {
        return preferences.getString("auth_token", null)
    }

    /*
     * Elimina el token almacenado.
     * Se utilizará cuando el usuario cierre sesión.
     */
    fun clearToken() {
        preferences.edit()
            .remove("auth_token")
            .apply()
    }
}
