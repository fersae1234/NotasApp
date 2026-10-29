package com.example.notasapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notasapp.repository.NotesRepository

/*
 * Fábrica encargada de crear NotesViewModel.
 *
 * Android utiliza ViewModelProvider para
 * administrar el ciclo de vida de los ViewModel.
 *
 * Como nuestro NotesViewModel necesita recibir
 * un NotesRepository en su constructor,
 * utilizamos esta fábrica para proporcionárselo.
 */
class NotesViewModelFactory(
    private val repository: NotesRepository
) : ViewModelProvider.Factory {

    /*
     * Crear una instancia del ViewModel solicitado.
     *
     * Verificamos que corresponda a NotesViewModel
     * y le enviamos el repositorio necesario.
     */
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repository) as T
        }

        /*
         * Si se solicita otro tipo de ViewModel,
         * mostramos un error para identificar
         * el problema durante el desarrollo.
         */
        throw IllegalArgumentException(
            "Clase ViewModel desconocida"
        )
    }
}
