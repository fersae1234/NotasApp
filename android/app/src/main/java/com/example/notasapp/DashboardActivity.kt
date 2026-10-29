package com.example.notasapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.notasapp.network.TokenManager

/*
 * Dashboard principal de NotasApp.
 *
 * Esta pantalla se muestra después de iniciar
 * sesión correctamente.
 *
 * Permite acceder a la creación de notas,
 * consultar las notas guardadas y cerrar sesión.
 */
class DashboardActivity : AppCompatActivity() {

    // Elementos visuales del Dashboard.
    private lateinit var txtBienvenida: TextView
    private lateinit var btnCrearNota: Button
    private lateinit var btnVerNotas: Button
    private lateinit var btnDashboardSalir: Button

    // Administrador del token de autenticación.
    private lateinit var tokenManager: TokenManager

    /*
     * Inicializar la pantalla principal.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar el diseño XML del Dashboard.
        setContentView(R.layout.activity_dashboard)

        // Relacionar los elementos visuales.
        txtBienvenida = findViewById(R.id.txtBienvenida)
        btnCrearNota = findViewById(R.id.btnCrearNota)
        btnVerNotas = findViewById(R.id.btnVerNotas)
        btnDashboardSalir = findViewById(R.id.btnDashboardSalir)

        // Inicializar el administrador del token.
        tokenManager = TokenManager(this)

        /*
         * Recuperar el nombre enviado desde Login.
         *
         * Este nombre proviene de la respuesta
         * de autenticación de nuestro backend.
         */
        val userName = intent.getStringExtra("USER_NAME")

        txtBienvenida.text = if (!userName.isNullOrBlank()) {
            "¡Bienvenido, $userName!"
        } else {
            "¡Bienvenido a NotasApp!"
        }

        /*
         * Abrir la pantalla independiente
         * de creación de notas.
         *
         * La crearemos en los próximos pasos.
         */
        btnCrearNota.setOnClickListener {

            val intent = Intent(
                this,
                CreateNoteActivity::class.java
            )

            startActivity(intent)
        }

        /*
         * Abrir la pantalla que muestra
         * las notas almacenadas en SQLite.
         */
        btnVerNotas.setOnClickListener {

            val intent = Intent(
                this,
                NotesActivity::class.java
            )

            startActivity(intent)
        }

        // Configurar el botón para cerrar sesión.
        btnDashboardSalir.setOnClickListener {
            confirmLogout()
        }
    }

    /*
     * Solicitar confirmación antes de cerrar sesión.
     */
    private fun confirmLogout() {

        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Deseas salir de NotasApp?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Salir") { _, _ ->

                // Eliminar el token JWT almacenado.
                tokenManager.clearToken()

                // Regresar a la pantalla de Login.
                val intent = Intent(
                    this,
                    MainActivity::class.java
                )

                /*
                 * Limpiar el historial para impedir
                 * regresar a una pantalla protegida
                 * utilizando el botón Atrás.
                 */
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()
            }
            .show()
    }
}
