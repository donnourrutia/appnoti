package com.example.notigoal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notigoal.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// Reutilizamos el mismo estado de UI que ya tenemos
class TeamMatchesViewModel : ViewModel() {

    private val apiKey = "0fbe3a43da834080a6be071fc33521d6" // Tu API Key

    private val _uiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val uiState: StateFlow<MatchesUiState> = _uiState

    init {
        // Pedimos los partidos del Real Madrid (ID 86) al iniciar
        fetchTeamMatches(86)
    }

    private fun fetchTeamMatches(teamId: Int) {
        viewModelScope.launch {
            _uiState.value = MatchesUiState.Loading
            try {
                val response = RetrofitInstance.api.getTeamMatches(apiKey, teamId)
                if (response.isSuccessful) {
                    val matches = response.body()?.matches ?: emptyList()
                    _uiState.value = MatchesUiState.Success(matches)
                } else {
                    _uiState.value = MatchesUiState.Error
                }
            } catch (e: IOException) {
                _uiState.value = MatchesUiState.Error
            } catch (e: retrofit2.HttpException) {
                _uiState.value = MatchesUiState.Error
            }
        }
    }
}
