package com.example.notigoal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notigoal.data.model.Match
import com.example.notigoal.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// Sealed interface para representar los posibles estados de la UI
sealed interface MatchesUiState {
    data class Success(val matches: List<Match>) : MatchesUiState
    object Error : MatchesUiState
    object Loading : MatchesUiState
}

class MatchesViewModel : ViewModel() {

    // Estado interno mutable
    private val _uiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    // Estado público inmutable que la UI observará
    val uiState: StateFlow<MatchesUiState> = _uiState

    // El bloque init se ejecuta cuando se crea el ViewModel
    init {
        fetchMatches()
    }

    /**
     * Lanza una corrutina para obtener los partidos de la API.
     */
    private fun fetchMatches() {
        viewModelScope.launch {
            _uiState.value = MatchesUiState.Loading
            _uiState.value = try {
                val response = RetrofitInstance.api.getMatches()
                MatchesUiState.Success(response.matches)
            } catch (e: IOException) {
                MatchesUiState.Error
            } catch (e: retrofit2.HttpException) {
                MatchesUiState.Error
            }
        }
    }
}