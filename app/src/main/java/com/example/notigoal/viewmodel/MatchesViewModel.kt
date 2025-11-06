package com.example.notigoal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notigoal.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Este es el "Cerebro" de nuestra app (el ViewModel).
 * Su trabajo es:
 * 1. Pedir los datos (al "motor" Retrofit).
 * 2. Guardar el estado de la UI (Cargando, Éxito, Error).
 * 3. Sobrevivir a cambios de configuración (ej: rotar el teléfono).
 */
class MatchesViewModel : ViewModel() {

    // _uiState es el estado INTERNO y mutable (solo el ViewModel puede cambiarlo).
    // Inicia en estado 'Loading'.
    private val _uiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)

    // uiState es el estado PÚBLICO y de solo lectura.
    // La UI (nuestros Composables) observará este 'StateFlow' para pintarse.
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    /**
     * El bloque 'init' se ejecuta automáticamente
     * la primera vez que se crea el ViewModel.
     */
    init {
        // Lanzamos la petición de red para buscar los partidos.
        fetchMatches()
    }

    /**
     * Lanza una 'coroutine' (un hilo secundario optimizado)
     * para hacer la llamada de red sin bloquear la UI.
     */
    private fun fetchMatches() {
        viewModelScope.launch {
            try {
                // 1. Pedimos los partidos a nuestra API (la instancia de Retrofit)
                val response = RetrofitInstance.api.getMatches()

                // 2. Si la llamada es exitosa, actualizamos el estado a 'Success'
                //    y le pasamos la lista de partidos.
                _uiState.value = MatchesUiState.Success(response.matches)

            } catch (e: IOException) {
                // Error de red (ej: sin internet)
                _uiState.value = MatchesUiState.Error("Error de red: ${e.message}")
            } catch (e: Exception) {
                // Otro tipo de error (ej: la API falló, el JSON es incorrecto)
                _uiState.value = MatchesUiState.Error("Error: ${e.message}")
            }
        }
    }
}