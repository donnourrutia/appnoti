package com.example.notigoal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notigoal.data.db.FavoriteTeam
import com.example.notigoal.data.model.Team
import com.example.notigoal.data.remote.RetrofitInstance
import com.example.notigoal.data.repository.FavoriteTeamsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.io.IOException

// Estado de la UI para la selección de equipos
sealed interface TeamSelectionUiState {
    data class Success(val teams: List<Team>, val favoriteTeamIds: Set<Int>) : TeamSelectionUiState
    object Error : TeamSelectionUiState
    object Loading : TeamSelectionUiState
}

class TeamSelectionViewModel(
    private val favoriteTeamsRepository: FavoriteTeamsRepository
) : ViewModel() {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    private val _uiState = MutableStateFlow<TeamSelectionUiState>(TeamSelectionUiState.Loading)
    val uiState: StateFlow<TeamSelectionUiState> = _uiState

    init {
        fetchTeams()

        // Combina la lista de todos los equipos y los IDs de los equipos favoritos
        // para crear el estado final de la UI.
        _teams.combine(favoriteTeamsRepository.getAllFavoriteTeams()) {
                teams, favoriteTeams ->
            val favoriteTeamIds = favoriteTeams.map { it.id }.toSet()
            if (teams.isNotEmpty()) {
                TeamSelectionUiState.Success(teams, favoriteTeamIds)
            } else if (_uiState.value is TeamSelectionUiState.Error) {
                // Mantener el estado de error si ya lo teníamos
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
            _uiState.value = TeamSelectionUiState.Loading // Asegura que el estado de carga se active
            try {
                val competitionIds = listOf("PD", "PL", "BL1", "SA", "FL1", "CL") // Códigos de ligas populares
                val allFetchedTeams = mutableListOf<Team>()

                for (id in competitionIds) {
                    try {
                        val response = RetrofitInstance.api.getTeamsInCompetition(id)
                        if (response.isSuccessful) {
                            response.body()?.teams?.let { teams ->
                                allFetchedTeams.addAll(teams)
                            }
                        } else {
                        }
                    } catch (e: IOException) {

                    } catch (e: retrofit2.HttpException) {
                    }
                }

                if (allFetchedTeams.isNotEmpty()) {
                    // Filtrar duplicados por ID de equipo
                    _teams.value = allFetchedTeams.distinctBy { it.id }
                } else {
                    // Si no se pudo cargar ningún equipo de ninguna liga
                    _uiState.value = TeamSelectionUiState.Error
                }

            } catch (e: Exception) {
                // Captura cualquier otra excepción inesperada
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
