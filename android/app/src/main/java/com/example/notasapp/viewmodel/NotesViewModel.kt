package com.example.notasapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notasapp.model.Note
import com.example.notasapp.repository.NotesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException

/*
 * ViewModel encargado de controlar la pantalla de notas.
 *
 * Se comunica con NotesRepository para consultar,
 * crear, actualizar y eliminar notas del usuario.
 *
 * Utiliza LiveData para informar a la interfaz
 * cuando cambian los datos o sucede algún error.
 */
class NotesViewModel(
    private val repository: NotesRepository
) : ViewModel() {

    // Lista de notas que mostraremos en pantalla.
    private val _notes = MutableLiveData<List<Note>>(emptyList())
    val notes: LiveData<List<Note>> = _notes

    // Indica si estamos esperando una respuesta del servidor.
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Mensaje que confirma una operación exitosa.
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    // Mensaje que informa si ocurrió algún problema.
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /*
     * READ: Consultar las notas del usuario.
     *
     * Solicita la información al backend
     * y actualiza la lista que observa la pantalla.
     */
    fun loadNotes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.getNotes()

                if (response.isSuccessful) {
                    val result = response.body()

                    if (result != null) {
                        _notes.value = result.notes
                    } else {
                        _errorMessage.value =
                            "El servidor devolvió una respuesta vacía."
                    }
                } else {
                    _errorMessage.value =
                        "No se pudieron cargar las notas. Código: ${response.code()}"
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _errorMessage.value =
                    "No se pudo conectar con el servidor."
            } catch (e: Exception) {
                _errorMessage.value =
                    "Ocurrió un error al cargar las notas."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /*
     * CREATE: Crear una nota nueva.
     *
     * Valida los datos y envía la solicitud
     * al repositorio para guardar la nota.
     */
    fun createNote(title: String, content: String) {
        if (title.isBlank()) {
            _errorMessage.value = "Escribe un título para la nota."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.createNote(
                    title.trim(),
                    content.trim()
                )

                if (response.isSuccessful) {
                    _successMessage.value = "Nota creada correctamente."
                    loadNotes()
                } else {
                    _errorMessage.value =
                        "No se pudo crear la nota. Código: ${response.code()}"
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _errorMessage.value =
                    "No se pudo conectar con el servidor."
            } catch (e: Exception) {
                _errorMessage.value =
                    "Ocurrió un error al crear la nota."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /*
     * UPDATE: Modificar una nota existente.
     *
     * Envía el identificador, título y contenido
     * de la nota que queremos actualizar.
     */
    fun updateNote(id: Int, title: String, content: String) {
        if (title.isBlank()) {
            _errorMessage.value = "Escribe un título para la nota."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.updateNote(
                    id,
                    title.trim(),
                    content.trim()
                )

                if (response.isSuccessful) {
                    _successMessage.value = "Nota actualizada correctamente."
                    loadNotes()
                } else {
                    _errorMessage.value =
                        "No se pudo actualizar la nota. Código: ${response.code()}"
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _errorMessage.value =
                    "No se pudo conectar con el servidor."
            } catch (e: Exception) {
                _errorMessage.value =
                    "Ocurrió un error al actualizar la nota."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /*
     * DELETE: Eliminar una nota.
     *
     * Solicita al backend eliminar la nota
     * y vuelve a cargar la lista actualizada.
     */
    fun deleteNote(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.deleteNote(id)

                if (response.isSuccessful) {
                    _successMessage.value = "Nota eliminada correctamente."
                    loadNotes()
                } else {
                    _errorMessage.value =
                        "No se pudo eliminar la nota. Código: ${response.code()}"
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _errorMessage.value =
                    "No se pudo conectar con el servidor."
            } catch (e: Exception) {
                _errorMessage.value =
                    "Ocurrió un error al eliminar la nota."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /*
     * Limpiar los mensajes después de mostrarlos
     * para evitar repetir notificaciones anteriores.
     */
    fun clearMessages() {
        _successMessage.value = null
        _errorMessage.value = null
    }
}
