package com.example.notigoal.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notigoal.data.model.Match
import com.example.notigoal.data.remote.RetrofitInstance
import com.example.notigoal.data.repository.FavoriteTeamsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

sealed interface MatchesUiState {
    data class Success(val matches: List<Match>) : MatchesUiState
    object Error : MatchesUiState
    object Loading : MatchesUiState
}

class MatchesViewModel(
    private val favoriteTeamsRepository: FavoriteTeamsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val uiState: StateFlow<MatchesUiState> = _uiState

    val favoriteTeams = favoriteTeamsRepository.getAllFavoriteTeams()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        fetchMatches()
    }

    private fun fetchMatches() {
        viewModelScope.launch {
            _uiState.value = MatchesUiState.Loading
            try {
                // Obtenemos la instancia de la API una vez
                val api = RetrofitInstance.api

                // 1. Lanzamos todas las llamadas de red en PARALELO usando async
                val deferredLaLiga = async { api.getLaLigaMatches() }
                val deferredPremier = async { api.getPremierLeagueMatches() }
                val deferredChampions = async { api.getChampionsLeagueMatches() }
                val deferredBundesliga = async { api.getBundesligaMatches() }
                val deferredSerieA = async { api.getSerieAMatches() }
                val deferredLigue1 = async { api.getLigue1Matches() }
                val deferredPrimeira = async { api.getPrimeiraLigaMatches() }
                val deferredWorldCup = async { api.getWorldCupMatches() }

                // 2. Esperamos a que TODAS las llamadas terminen
                val responses = awaitAll(
                    deferredLaLiga, deferredPremier, deferredChampions,
                    deferredBundesliga, deferredSerieA, deferredLigue1,
                    deferredPrimeira, deferredWorldCup
                )

                // 3. Juntamos todas las listas de partidos en una sola
                val allMatches = responses
                    .filter { it.isSuccessful && it.body() != null } // Filtramos solo las exitosas
                    .flatMap { it.body()!!.matches } // Extraemos las listas y las aplanamos
                    .sortedBy { it.utcDate } // Ordenamos la lista combinada por fecha

                Log.d("MatchesViewModel", "Se cargaron ${allMatches.size} partidos de ${responses.size} competiciones.")

                // 4. Actualizamos la UI con la lista combinada
                if (allMatches.isNotEmpty()) {
                    _uiState.value = MatchesUiState.Success(allMatches)
                } else {
                    // Si después de todo, la lista está vacía (quizás no hay partidos hoy)
                    _uiState.value = MatchesUiState.Success(emptyList()) // Mostrará "No hay partidos"
                }

            } catch (e: IOException) {
                Log.e("MatchesViewModel", "Error de red: ${e.message}", e)
                _uiState.value = MatchesUiState.Error
            } catch (e: retrofit2.HttpException) {
                Log.e("MatchesViewModel", "Error HTTP: ${e.message}", e)
                _uiState.value = MatchesUiState.Error
            } catch (e: Exception) {
                Log.e("MatchesViewModel", "Error inesperado: ${e.message}", e)
                _uiState.value = MatchesUiState.Error
            }
        }
    }
}