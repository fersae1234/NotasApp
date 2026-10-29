package com.example.notasapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.notasapp.network.RetrofitClient
import com.example.notasapp.repository.NotesRepository
import com.example.notasapp.viewmodel.NotesViewModel
import com.example.notasapp.viewmodel.NotesViewModelFactory

/*
 * Pantalla independiente para crear notas.
 *
 * Utiliza ConstraintLayout mediante el archivo
 * activity_create_note.xml.
 *
 * Se comunica con NotesViewModel para guardar
 * la nota en el backend mediante Retrofit.
 */
class CreateNoteActivity : AppCompatActivity() {

    // ViewModel que administra las operaciones de notas.
    private lateinit var notesViewModel: NotesViewModel

    // Elementos del formulario.
    private lateinit var editTituloNota: EditText
    private lateinit var editContenidoNota: EditText
    private lateinit var btnGuardarNota: Button
    private lateinit var btnCancelarNota: Button
    private lateinit var progressGuardarNota: ProgressBar

    /*
     * Inicializar la pantalla de creación.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cargar el diseño XML del formulario.
        setContentView(R.layout.activity_create_note)

        // Relacionar los elementos XML con Kotlin.
        editTituloNota = findViewById(R.id.editTituloNota)
        editContenidoNota = findViewById(R.id.editContenidoNota)
        btnGuardarNota = findViewById(R.id.btnGuardarNota)
        btnCancelarNota = findViewById(R.id.btnCancelarNota)
        progressGuardarNota = findViewById(R.id.progressGuardarNota)

        /*
         * Configurar las dependencias MVVM.
         *
         * RetrofitClient administra la conexión HTTP
         * y agrega automáticamente el token JWT.
         */
        val apiService = RetrofitClient.create(this)
        val repository = NotesRepository(apiService)
        val factory = NotesViewModelFactory(repository)

        notesViewModel = ViewModelProvider(
            this,
            factory
        )[NotesViewModel::class.java]

        /*
         * Guardar una nueva nota.
         *
         * Obtenemos el título y contenido
         * escritos por el usuario.
         */
        btnGuardarNota.setOnClickListener {

            val title = editTituloNota.text.toString().trim()
            val content = editContenidoNota.text.toString().trim()

            // Validar que exista un título.
            if (title.isBlank()) {
                editTituloNota.error = "Escribe un título para la nota."
                editTituloNota.requestFocus()
            } else {

                // Solicitar al ViewModel guardar la nota.
                notesViewModel.createNote(title, content)
            }
        }

        /*
         * Cancelar la creación y regresar
         * a la pantalla anterior.
         */
        btnCancelarNota.setOnClickListener {
            finish()
        }

        /*
         * Observar el estado de carga.
         *
         * Mientras se guarda la nota, mostramos
         * el ProgressBar y desactivamos el botón.
         */
        notesViewModel.isLoading.observe(this) { isLoading ->

            progressGuardarNota.visibility =
                if (isLoading) View.VISIBLE else View.GONE

            btnGuardarNota.isEnabled = !isLoading
            btnCancelarNota.isEnabled = !isLoading
        }

        /*
         * Observar los mensajes de éxito.
         *
         * Cuando el backend confirma que la nota
         * fue creada, mostramos una notificación
         * y regresamos al Dashboard.
         */
        notesViewModel.successMessage.observe(this) { message ->

            if (!message.isNullOrEmpty()) {

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()

                // Limpiar el mensaje para evitar duplicados.
                notesViewModel.clearMessages()

                // Regresar a la pantalla anterior.
                finish()
            }
        }

        /*
         * Observar los errores.
         *
         * Si falla la solicitud al backend,
         * mostramos un mensaje al usuario.
         */
        notesViewModel.errorMessage.observe(this) { error ->

            if (!error.isNullOrEmpty()) {

                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_LONG
                ).show()

                notesViewModel.clearMessages()
            }
        }
    }
}
