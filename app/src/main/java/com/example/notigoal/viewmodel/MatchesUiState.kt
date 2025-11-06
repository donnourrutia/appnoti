package com.example.notigoal.viewmodel

import com.example.notigoal.data.model.Match

/**
 * Esta es una 'sealed interface' (interfaz sellada).
 * Define los ÚNICOS estados posibles en los que puede
 * estar nuestra pantalla de partidos.
 */
sealed interface MatchesUiState {
    /**
     * La pantalla está cargando datos (ej: mostrando un círculo de carga).
     */
    data object Loading : MatchesUiState

    /**
     * Los datos se cargaron con éxito.
     * Contiene la lista de partidos.
     */
    data class Success(val matches: List<Match>) : MatchesUiState

    /**
     * Ocurrió un error al cargar los datos.
     * Contiene un mensaje de error para mostrar.
     */
    data class Error(val message: String) : MatchesUiState
}