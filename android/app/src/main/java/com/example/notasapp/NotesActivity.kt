package com.example.notasapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notasapp.adapter.NotesAdapter
import com.example.notasapp.model.Note
import com.example.notasapp.network.RetrofitClient
import com.example.notasapp.network.TokenManager
import com.example.notasapp.repository.NotesRepository
import com.example.notasapp.viewmodel.NotesViewModel
import com.example.notasapp.viewmodel.NotesViewModelFactory

/*
 * Pantalla principal de notas del usuario.
 *
 * Permite visualizar las notas almacenadas
 * en nuestro backend Express y SQLite.
 *
 * También administra las acciones de crear,
 * editar, eliminar notas y cerrar sesión.
 */
class NotesActivity : AppCompatActivity() {

    // Componentes visuales de la pantalla.
    private lateinit var recyclerNotes: RecyclerView
    private lateinit var progressNotes: ProgressBar
    private lateinit var layoutSinNotas: LinearLayout
    private lateinit var btnNuevaNota: Button
    private lateinit var btnCerrarSesion: Button

    // Adaptador que mostrará las notas.
    private lateinit var notesAdapter: NotesAdapter

    // ViewModel que administra las operaciones CRUD.
    private lateinit var notesViewModel: NotesViewModel

    // Administrador del token de autenticación.
    private lateinit var tokenManager: TokenManager

    /*
     * Inicializar la pantalla y sus componentes.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notes)

        // Obtener las referencias de la interfaz XML.
        recyclerNotes = findViewById(R.id.recyclerNotas)
        progressNotes = findViewById(R.id.progressNotas)
        layoutSinNotas = findViewById(R.id.layoutSinNotas)
        btnNuevaNota = findViewById(R.id.btnNuevaNota)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        // Recuperar el token almacenado en el dispositivo.
        tokenManager = TokenManager(this)

        /*
         * Preparar Retrofit, el repositorio
         * y el ViewModel de las notas.
         */
        val apiService = RetrofitClient.create(this)
        val repository = NotesRepository(apiService)
        val factory = NotesViewModelFactory(repository)

        notesViewModel = ViewModelProvider(
            this,
            factory
        )[NotesViewModel::class.java]

        // Configurar la lista de notas.
        setupRecyclerView()

        // Observar los cambios enviados por el ViewModel.
        observeViewModel()

        // Configurar los botones principales.
        setupButtons()

        // Consultar las notas al abrir la pantalla.
        notesViewModel.loadNotes()
    }

    /*
     * Configurar RecyclerView para mostrar
     * las notas en una lista vertical.
     */
    private fun setupRecyclerView() {

        notesAdapter = NotesAdapter(
            onEditClick = { note ->
                showNoteDialog(note)
            },
            onDeleteClick = { note ->
                confirmDeleteNote(note)
            }
        )

        recyclerNotes.layoutManager = LinearLayoutManager(this)
        recyclerNotes.adapter = notesAdapter
    }

    /*
     * Observar las notas, el indicador de carga
     * y los mensajes del ViewModel.
     */
    private fun observeViewModel() {

        notesViewModel.notes.observe(this) { notes ->

            // Actualizar las tarjetas del RecyclerView.
            notesAdapter.updateNotes(notes)

            // Mostrar un mensaje si todavía no hay notas.
            layoutSinNotas.visibility =
                if (notes.isEmpty()) View.VISIBLE else View.GONE
        }

        notesViewModel.isLoading.observe(this) { loading ->

            progressNotes.visibility =
                if (loading) View.VISIBLE else View.GONE
        }

        notesViewModel.successMessage.observe(this) { message ->

            if (!message.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_SHORT
                ).show()

                notesViewModel.clearMessages()
            }
        }

        notesViewModel.errorMessage.observe(this) { message ->

            if (!message.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()

                notesViewModel.clearMessages()
            }
        }
    }

    /*
     * Configurar los botones de nueva nota
     * y cierre de sesión.
     */
    private fun setupButtons() {

        btnNuevaNota.setOnClickListener {
            showNoteDialog(null)
        }

        btnCerrarSesion.setOnClickListener {
            confirmLogout()
        }
    }

    /*
     * Mostrar un formulario para crear o editar notas.
     *
     * Si note es null, creamos una nota nueva.
     * Si recibimos una nota, cargamos sus datos
     * para permitir su modificación.
     */
    private fun showNoteDialog(note: Note?) {

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 0)
        }

        val inputTitle = EditText(this).apply {
            hint = "Título de la nota"
            setSingleLine(true)
            setText(note?.title ?: "")
        }

        val inputContent = EditText(this).apply {
            hint = "Contenido de la nota"
            minLines = 4
            maxLines = 8
            gravity = android.view.Gravity.TOP
            setText(note?.content ?: "")
        }

        container.addView(inputTitle)
        container.addView(inputContent)

        val isEditing = note != null

        val dialog = AlertDialog.Builder(this)
            .setTitle(
                if (isEditing) "Editar nota" else "Nueva nota"
            )
            .setView(container)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton(
                if (isEditing) "Guardar cambios" else "Guardar",
                null
            )
            .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val title = inputTitle.text.toString().trim()
                val content = inputContent.text.toString().trim()

                // Validar que la nota tenga un título.
                if (title.isBlank()) {
                    inputTitle.error = "Ingresa un título"
                    return@setOnClickListener
                }

                if (note == null) {

                    // Crear una nota nueva.
                    notesViewModel.createNote(title, content)

                } else {

                    // Actualizar la nota seleccionada.
                    notesViewModel.updateNote(
                        note.id,
                        title,
                        content
                    )
                }

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    /*
     * Solicitar confirmación antes de eliminar
     * una nota para evitar borrarla accidentalmente.
     */
    private fun confirmDeleteNote(note: Note) {

        AlertDialog.Builder(this)
            .setTitle("Eliminar nota")
            .setMessage(
                "¿Deseas eliminar la nota \"${note.title}\"?"
            )
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->

                notesViewModel.deleteNote(note.id)
            }
            .show()
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

                // Eliminar el token almacenado.
                tokenManager.clearToken()

                // Regresar a la pantalla de inicio de sesión.
                val intent = Intent(
                    this,
                    MainActivity::class.java
                )

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                finish()
            }
            .show()
    }
}
