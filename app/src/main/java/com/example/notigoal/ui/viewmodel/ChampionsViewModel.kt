package com.example.notigoal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notigoal.data.remote.RetrofitInstance
import com.example.notigoal.data.repository.FavoriteTeamsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

// NOTA: Reutilizamos la misma clase 'MatchesUiState' que ya teníamos.
class ChampionsViewModel(
    private val favoriteTeamsRepository: FavoriteTeamsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val uiState: StateFlow<MatchesUiState> = _uiState

    // Expone los equipos favoritos como un StateFlow
    val favoriteTeams = favoriteTeamsRepository.getAllFavoriteTeams()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000), // Empieza a recolectar cuando haya suscriptores y se detiene 5s después del último
            initialValue = emptyList() // Valor inicial mientras se carga
        )

    init {
        fetchChampionsMatches()
    }

    private fun fetchChampionsMatches() {
        viewModelScope.launch {
            _uiState.value = MatchesUiState.Loading
            try {
                val response = RetrofitInstance.api.getChampionsLeagueMatches()

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
