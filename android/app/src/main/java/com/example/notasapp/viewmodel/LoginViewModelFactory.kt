package com.example.notasapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notasapp.repository.AuthRepository

/*
 * Clase encargada de crear una instancia
 * de LoginViewModel.
 *
 * Permite proporcionar el AuthRepository
 * que necesita el ViewModel para funcionar.
 *
 * Esta técnica se conoce como inyección
 * de dependencias mediante Factory.
 */
class LoginViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {

    /*
     * Función que crea el ViewModel solicitado.
     *
     * Comprueba que la clase solicitada
     * corresponda a LoginViewModel.
     */
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        // Verifica el tipo de ViewModel solicitado.
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {

            // Crea el LoginViewModel con su Repository.
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }

        // Informa si se solicita un ViewModel desconocido.
        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}
