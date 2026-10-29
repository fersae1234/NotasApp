package com.example.notasapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.notasapp.network.RetrofitClient
import com.example.notasapp.network.TokenManager
import com.example.notasapp.repository.AuthRepository
import com.example.notasapp.viewmodel.LoginViewModel
import com.example.notasapp.viewmodel.LoginViewModelFactory

/*
 * Actividad principal de NotasApp.
 *
 * Muestra la pantalla de inicio de sesión
 * y conecta los elementos visuales con LoginViewModel.
 *
 * Utiliza la arquitectura MVVM para mantener
 * separada la interfaz de la lógica de autenticación.
 *
 * Cuando el usuario inicia sesión correctamente,
 * abre DashboardActivity y envía su nombre.
 */
class MainActivity : AppCompatActivity() {

    // ViewModel encargado de gestionar el inicio de sesión.
    private lateinit var loginViewModel: LoginViewModel

    // Elementos visuales de la pantalla.
    private lateinit var editUsuario: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressLogin: ProgressBar

    /*
     * Método que se ejecuta al abrir la actividad.
     *
     * Inicializa la interfaz, configura el ViewModel
     * y establece los eventos del botón.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar el diseño XML de inicio de sesión.
        setContentView(R.layout.activity_main)

        /*
         * Relacionar los elementos XML con Kotlin
         * utilizando sus identificadores.
         */
        editUsuario = findViewById(R.id.editUsuario)
        editPassword = findViewById(R.id.editPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressLogin = findViewById(R.id.progressLogin)

        /*
         * Configurar las dependencias necesarias.
         *
         * RetrofitClient crea el servicio HTTP.
         * TokenManager administra el token JWT.
         * AuthRepository gestiona la autenticación.
         */
        val apiService = RetrofitClient.create(this)
        val tokenManager = TokenManager(this)

        val repository = AuthRepository(
            apiService,
            tokenManager
        )

        /*
         * Crear el ViewModel mediante una Factory.
         *
         * Esto permite proporcionar el Repository
         * al constructor de LoginViewModel.
         */
        val factory = LoginViewModelFactory(repository)

        loginViewModel = ViewModelProvider(
            this,
            factory
        )[LoginViewModel::class.java]

        /*
         * Configurar el botón de inicio de sesión.
         *
         * Obtiene el correo y contraseña ingresados
         * y solicita la autenticación al ViewModel.
         */
        btnLogin.setOnClickListener {

            val email = editUsuario.text.toString()
            val password = editPassword.text.toString()

            loginViewModel.login(email, password)
        }

        /*
         * Observar los cambios en el estado de carga.
         *
         * Mostrar el ProgressBar mientras se procesa
         * la solicitud y desactivar temporalmente el botón.
         */
        loginViewModel.isLoading.observe(this) { isLoading ->

            progressLogin.visibility =
                if (isLoading) View.VISIBLE else View.GONE

            btnLogin.isEnabled = !isLoading
        }

        /*
         * Observar los mensajes de error.
         *
         * Si ocurre un problema de autenticación,
         * mostrar un mensaje mediante Toast.
         */
        loginViewModel.errorMessage.observe(this) { error ->

            if (!error.isNullOrEmpty()) {

                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        /*
         * Observar el resultado del inicio de sesión.
         *
         * Si el backend devuelve una respuesta válida,
         * mostrar un mensaje de bienvenida y abrir
         * el Dashboard principal de NotasApp.
         */
        loginViewModel.loginResult.observe(this) { result ->

            if (result != null) {

                // Mostrar el nombre del usuario autenticado.
                Toast.makeText(
                    this,
                    "Bienvenido, ${result.user.name}",
                    Toast.LENGTH_LONG
                ).show()

                /*
                 * Crear una intención para abrir
                 * DashboardActivity.
                 */
                val intent = Intent(
                    this,
                    DashboardActivity::class.java
                )

                /*
                 * Enviar el nombre del usuario obtenido
                 * desde nuestro backend.
                 *
                 * DashboardActivity recuperará este dato
                 * para mostrar el mensaje de bienvenida.
                 */
                intent.putExtra(
                    "USER_NAME",
                    result.user.name
                )

                /*
                 * Limpiar el historial de actividades
                 * para evitar regresar al Login
                 * utilizando el botón Atrás.
                 */
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                // Abrir el Dashboard principal.
                startActivity(intent)

                // Finalizar la pantalla de Login.
                finish()
            }
        }
    }
}
