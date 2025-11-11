package com.example.notigoal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notigoal.data.remote.RetrofitInstance
import com.example.notigoal.data.repository.FavoriteTeamsRepository
import com.example.notigoal.data.preferences.UserPreferencesRepository
import com.example.notigoal.data.preferences.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

// Reutilizamos el mismo estado de UI que ya tenemos para los partidos
class TeamMatchesViewModel(
    private val favoriteTeamsRepository: FavoriteTeamsRepository,
    private val userPreferencesRepository: UserPreferencesRepository // Inyectamos UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val uiState: StateFlow<MatchesUiState> = _uiState

    // Expone los equipos favoritos como un StateFlow
    val favoriteTeams = favoriteTeamsRepository.getAllFavoriteTeams()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList() // Valor inicial mientras se carga
        )

    // Expone los datos del perfil del usuario como un StateFlow
    val userProfile: StateFlow<UserProfile> = userPreferencesRepository.userProfileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = UserProfile("", "", "") // Valor inicial mientras se carga
        )

    init {
        // Pedimos los partidos del Real Madrid (ID 86) al iniciar
        fetchTeamMatches(86)

        // Ya no necesitamos un collect aquí, ya que userProfile está usando stateIn
    }

    private fun fetchTeamMatches(teamId: Int) {
        viewModelScope.launch {
            _uiState.value = MatchesUiState.Loading
            try {
                val response = RetrofitInstance.api.getTeamMatches(teamId)
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
