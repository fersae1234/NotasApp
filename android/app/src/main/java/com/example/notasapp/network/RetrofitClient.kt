
package com.example.notasapp.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
 * Objeto encargado de configurar Retrofit y OkHttp.
 *
 * Retrofit permite consumir los endpoints REST.
 * OkHttp administra las solicitudes HTTP y agrega
 * automáticamente el token de autenticación.
 */
object RetrofitClient {

    /*
     * Dirección del backend de NotasApp.
     *
     * Utilizamos 127.0.0.1 porque configuramos
     * una redirección mediante ADB Reverse.
     *
     * El comando utilizado fue:
     * adb reverse tcp:3000 tcp:3000
     *
     * Esto permite que el emulador acceda
     * al servidor Node.js de nuestra computadora
     * utilizando el puerto 3000.
     *
     * Esta configuración es para desarrollo local.
     */
    private const val BASE_URL = "http://127.0.0.1:3000/"

    /*
     * Crea y configura el servicio de Retrofit.
     *
     * Recibe el contexto de Android para acceder
     * al token guardado por TokenManager.
     */
    fun create(context: Context): ApiService {

        val tokenManager = TokenManager(
            context.applicationContext
        )

        /*
         * Interceptor que agrega el token JWT
         * a las solicitudes cuando el usuario
         * ha iniciado sesión.
         */
        val authInterceptor = okhttp3.Interceptor { chain ->

            val originalRequest = chain.request()
            val token = tokenManager.getToken()

            val requestBuilder = originalRequest.newBuilder()

            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader(
                    "Authorization",
                    "Bearer $token"
                )
            }

            chain.proceed(requestBuilder.build())
        }

        /*
         * Registra información básica de las
         * solicitudes durante el desarrollo.
         *
         * No registra cuerpos ni encabezados
         * que puedan contener información sensible.
         */
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        /*
         * Configuramos el cliente HTTP.
         *
         * Agregamos los interceptores para
         * autenticación y diagnóstico.
         */
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        /*
         * Configuramos Retrofit.
         *
         * GsonConverterFactory convierte
         * automáticamente objetos Kotlin
         * a JSON y respuestas JSON a Kotlin.
         */
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        /*
         * Devolvemos el servicio configurado
         * para comunicarse con nuestro backend.
         */
        return retrofit.create(ApiService::class.java)
    }
}
