package com.example.notigoal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notigoal.data.db.FavoriteTeam
import com.example.notigoal.data.model.Team
import com.example.notigoal.data.remote.RetrofitInstance
import com.example.notigoal.data.repository.FavoriteTeamsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

// Estado de la UI para la selección de equipos
sealed interface TeamSelectionUiState {
    data class Success(val teams: List<Team>, val favoriteTeamIds: Set<Int>) : TeamSelectionUiState
    object Error : TeamSelectionUiState
    object Loading : TeamSelectionUiState
}

//open a la clase para poder heredarla en el test
open class TeamSelectionViewModel(
    private val favoriteTeamsRepository: FavoriteTeamsRepository
) : ViewModel() {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    private val _uiState = MutableStateFlow<TeamSelectionUiState>(TeamSelectionUiState.Loading)

    open val uiState: StateFlow<TeamSelectionUiState> = _uiState

    init {
        fetchTeams()

        // Combina la lista de todos los equipos y los IDs de los equipos favoritos
        _teams.combine(favoriteTeamsRepository.getAllFavoriteTeams()) {
                teams, favoriteTeams ->
            val favoriteTeamIds = favoriteTeams.map { it.id }.toSet()
            if (teams.isNotEmpty()) {
                TeamSelectionUiState.Success(teams, favoriteTeamIds)
            } else if (_uiState.value is TeamSelectionUiState.Error) {
                TeamSelectionUiState.Error
            } else {
                TeamSelectionUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TeamSelectionUiState.Loading
        ).let { combinedFlow ->
            viewModelScope.launch { combinedFlow.collect { _uiState.value = it } }
        }
    }

    private fun fetchTeams() {
        viewModelScope.launch {
            _uiState.value = TeamSelectionUiState.Loading
            try {
                val competitionIds = listOf("PD", "PL", "BL1", "SA", "FL1", "CL")
                val allFetchedTeams = mutableListOf<Team>()

                for (id in competitionIds) {
                    try {
                        val response = RetrofitInstance.api.getTeamsInCompetition(id)
                        if (response.isSuccessful) {
                            response.body()?.teams?.let { teams ->
                                allFetchedTeams.addAll(teams)
                            }
                        }
                    } catch (e: IOException) {
                        // Manejo de error de red
                    } catch (e: retrofit2.HttpException) {
                        // Manejo de error http
                    }
                }

                if (allFetchedTeams.isNotEmpty()) {
                    _teams.value = allFetchedTeams.distinctBy { it.id }
                } else {
                    _uiState.value = TeamSelectionUiState.Error
                }

            } catch (e: Exception) {
                _uiState.value = TeamSelectionUiState.Error
            }
        }
    }

    fun toggleFavoriteTeam(team: Team, isFavorite: Boolean) {
        viewModelScope.launch {
            val favoriteTeam = FavoriteTeam(
                id = team.id,
                name = team.name,
                shortName = team.shortName,
                crestUrl = team.crest
            )
            if (isFavorite) {
                favoriteTeamsRepository.removeFavorite(favoriteTeam)
            } else {
                favoriteTeamsRepository.addFavorite(favoriteTeam)
            }
        }
    }
}