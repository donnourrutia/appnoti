package com.example.notigoal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notigoal.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// NOTA: Reutilizamos la misma clase 'MatchesUiState' que ya teníamos.
class ChampionsViewModel : ViewModel() {

    private val apiKey = "0fbe3a43da834080a6be071fc33521d6" // <-- TU API KEY

    private val _uiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val uiState: StateFlow<MatchesUiState> = _uiState

    init {
        fetchChampionsMatches()
    }

    private fun fetchChampionsMatches() {
        viewModelScope.launch {
            _uiState.value = MatchesUiState.Loading
            try {
                // Llamamos a la función de la Champions League.
                val response = RetrofitInstance.api.getChampionsLeagueMatches(apiKey)

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

