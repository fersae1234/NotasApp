package com.example.notasapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notasapp.model.LoginResponse
import com.example.notasapp.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException

/*
 * ViewModel encargado de gestionar el inicio de sesión.
 *
 * Forma parte de la arquitectura MVVM.
 * Separa la lógica de autenticación de la interfaz
 * y utiliza LiveData para comunicar los resultados.
 *
 * También registra errores en Logcat para facilitar
 * la identificación de problemas durante el desarrollo.
 */
class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    /*
     * Etiqueta utilizada para identificar los mensajes
     * de diagnóstico en Logcat.
     */
    private val logTag = "NotasAppLogin"

    /*
     * Almacena el estado de carga del Login.
     *
     * true: se está procesando la solicitud.
     * false: la solicitud ha terminado.
     */
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /*
     * Almacena los datos recibidos cuando
     * el inicio de sesión es exitoso.
     */
    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    /*
     * Almacena los mensajes de error para
     * mostrarlos en la pantalla de Login.
     */
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /*
     * Función que inicia el proceso de autenticación.
     *
     * Primero valida que los campos no estén vacíos.
     * Después solicita al Repository que se comunique
     * con el backend mediante Retrofit.
     */
    fun login(email: String, password: String) {

        // Eliminamos espacios al inicio y final del correo.
        val cleanEmail = email.trim()

        // Comprobamos que los campos tengan información.
        if (cleanEmail.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Completa todos los campos"
            return
        }

        // Ejecutamos la solicitud mediante corrutinas.
        viewModelScope.launch {

            // Activamos el indicador de carga.
            _isLoading.value = true
            _errorMessage.value = null

            try {

                // Registramos el inicio de la solicitud.
                // No mostramos credenciales ni tokens.
                Log.d(
                    logTag,
                    "Iniciando solicitud de autenticación"
                )

                // Solicitamos al Repository iniciar sesión.
                val response = repository.login(
                    cleanEmail,
                    password
                )

                // Registramos el código HTTP recibido.
                Log.d(
                    logTag,
                    "Respuesta HTTP: ${response.code()}"
                )

                // Comprobamos la respuesta del backend.
                if (response.isSuccessful) {

                    val result = response.body()

                    if (
                        result != null &&
                        result.token.isNotBlank()
                    ) {

                        // Confirmamos que el Login fue exitoso.
                        Log.d(
                            logTag,
                            "Autenticación completada correctamente"
                        )

                        // Comunicamos el resultado a la interfaz.
                        _loginResult.value = result

                    } else {

                        // La respuesta no contiene datos válidos.
                        Log.e(
                            logTag,
                            "La respuesta no contiene un token válido"
                        )

                        _errorMessage.value =
                            "Respuesta inválida del servidor"
                    }

                } else {

                    /*
                     * Identificamos los errores HTTP
                     * sin mostrar información sensible.
                     */
                    Log.e(
                        logTag,
                        "Error HTTP durante el Login: ${response.code()}"
                    )

                    _errorMessage.value = when (response.code()) {
                        400 -> "Datos de inicio de sesión inválidos"
                        401 -> "Correo o contraseña incorrectos"
                        else -> "Error del servidor: ${response.code()}"
                    }
                }

            } catch (e: CancellationException) {

                /*
                 * Permitimos que las corrutinas canceladas
                 * terminen correctamente.
                 */
                throw e

            } catch (e: IOException) {

                /*
                 * Identificamos errores relacionados
                 * con la comunicación de red.
                 */
                Log.e(
                    logTag,
                    "Error de comunicación con el backend",
                    e
                )

                _errorMessage.value =
                    "No se pudo establecer conexión con el servidor"

            } catch (e: Exception) {

                /*
                 * Registramos otros errores, incluyendo
                 * posibles problemas al procesar JSON.
                 *
                 * El detalle técnico queda en Logcat.
                 */
                Log.e(
                    logTag,
                    "Error inesperado durante el Login",
                    e
                )

                _errorMessage.value =
                    "Ocurrió un error al procesar el inicio de sesión"

            } finally {

                // Ocultamos el indicador al finalizar.
                _isLoading.value = false
            }
        }
    }
}
