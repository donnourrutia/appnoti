package com.example.notigoal.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notigoal.data.model.Match
import com.example.notigoal.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// Estado para la pantalla de detalle
sealed interface MatchDetailUiState {
    data class Success(val match: Match) : MatchDetailUiState
    object Error : MatchDetailUiState
    object Loading : MatchDetailUiState
}

class MatchDetailViewModel(
    savedStateHandle: SavedStateHandle // Se usa para recibir argumentos de navegación
) : ViewModel() {

    private val apiKey = "0fbe3a43da834080a6be071fc33521d6" // Tu API Key
    private val matchId: Int = checkNotNull(savedStateHandle["matchId"])

    private val _uiState = MutableStateFlow<MatchDetailUiState>(MatchDetailUiState.Loading)
    val uiState: StateFlow<MatchDetailUiState> = _uiState

    init {
        fetchMatchDetails()
    }

    private fun fetchMatchDetails() {
        viewModelScope.launch {
            _uiState.value = MatchDetailUiState.Loading
            try {
                val response = RetrofitInstance.api.getMatchById(apiKey, matchId)
                if (response.isSuccessful) {
                    response.body()?.let { match ->
                        _uiState.value = MatchDetailUiState.Success(match)
                    } ?: run {
                        _uiState.value = MatchDetailUiState.Error
                    }
                } else {
                    _uiState.value = MatchDetailUiState.Error
                }
            } catch (e: IOException) {
                _uiState.value = MatchDetailUiState.Error
            } catch (e: retrofit2.HttpException) {
                _uiState.value = MatchDetailUiState.Error
            }
        }
    }
}
