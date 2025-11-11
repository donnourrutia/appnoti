package com.example.notigoal.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notigoal.data.db.FavoriteTeam
import com.example.notigoal.data.model.Match
import com.example.notigoal.data.remote.RetrofitInstance
import com.example.notigoal.data.repository.FavoriteTeamsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

// Estado para la pantalla de detalle
sealed interface MatchDetailUiState {
    data class Success(val match: Match, val isFavorite: Boolean, val simulatedHomeScore: Int, val simulatedAwayScore: Int) : MatchDetailUiState
    object Error : MatchDetailUiState
    object Loading : MatchDetailUiState
}

class MatchDetailViewModel(
    savedStateHandle: SavedStateHandle, // Se usa para recibir argumentos de navegación
    private val favoriteTeamsRepository: FavoriteTeamsRepository
) : ViewModel() {

    private val matchId: Int = checkNotNull(savedStateHandle["matchId"])

    // DECLARAR ESTAS VARIABLES ANTES DE uiState
    private val _simulatedHomeScore = MutableStateFlow(0)
    private val _simulatedAwayScore = MutableStateFlow(0)

    private val _uiState = MutableStateFlow<MatchDetailUiState>(MatchDetailUiState.Loading)
    // No exponemos _uiState directamente, sino un StateFlow combinado.
    val uiState: StateFlow<MatchDetailUiState> = combine(
        _uiState,
        favoriteTeamsRepository.getAllFavoriteTeams(),
        _simulatedHomeScore,
        _simulatedAwayScore
    ) { currentUiState, favoriteTeams, homeScore, awayScore ->
        when (currentUiState) {
            is MatchDetailUiState.Success -> {
                val isFavorite = favoriteTeams.any { team ->
                    team.id == currentUiState.match.homeTeam.id || team.id == currentUiState.match.awayTeam.id
                }
                currentUiState.copy(
                    isFavorite = isFavorite,
                    simulatedHomeScore = homeScore,
                    simulatedAwayScore = awayScore
                )
            }
            is MatchDetailUiState.Loading -> currentUiState
            is MatchDetailUiState.Error -> currentUiState
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MatchDetailUiState.Loading
    )

    init {
        fetchMatchDetails()
    }

    private fun fetchMatchDetails() {
        viewModelScope.launch {
            // Primero, establecer el estado de carga
            _uiState.value = MatchDetailUiState.Loading
            try {
                val response = RetrofitInstance.api.getMatchById(matchId)
                if (response.isSuccessful) {
                    response.body()?.let { match ->
                        // Inicializar los scores simulados con los scores reales si existen, o a 0
                        _simulatedHomeScore.value = match.score.fullTime?.home ?: 0
                        _simulatedAwayScore.value = match.score.fullTime?.away ?: 0

                        // Establecer el estado de éxito con los valores iniciales. El 'combine' se encargará del resto.
                        _uiState.value = MatchDetailUiState.Success(match, false, _simulatedHomeScore.value, _simulatedAwayScore.value)
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

    fun simulateHomeGoal() {
        _simulatedHomeScore.value++
    }

    fun simulateAwayGoal() {
        _simulatedAwayScore.value++
    }

    fun addTeamToFavorites(team: FavoriteTeam) {
        viewModelScope.launch {
            favoriteTeamsRepository.addFavorite(team)
        }
    }

    fun removeTeamFromFavorites(team: FavoriteTeam) {
        viewModelScope.launch {
            favoriteTeamsRepository.removeFavorite(team)
        }
    }
}
