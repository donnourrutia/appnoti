package com.example.notigoal.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notigoal.NotiGoalApplication
import com.example.notigoal.ui.viewmodel.ChampionsViewModel
import com.example.notigoal.ui.viewmodel.MatchDetailViewModel
import com.example.notigoal.ui.viewmodel.MatchesViewModel
import com.example.notigoal.ui.viewmodel.TeamMatchesViewModel
import com.example.notigoal.ui.viewmodel.TeamSelectionViewModel
import com.example.notigoal.ui.viewmodel.EditProfileViewModel // Nueva importación

/**
 * Fábrica (Factory) para crear todos los ViewModels de la app.
 * Esto nos permite inyectar el repositorio en sus constructores.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Inicializador para MatchesViewModel
        initializer {
            MatchesViewModel(
                favoriteTeamsRepository = notiGoalApplication().container.favoriteTeamsRepository
            )
        }
        // Inicializador para ChampionsViewModel
        initializer {
            ChampionsViewModel(
                favoriteTeamsRepository = notiGoalApplication().container.favoriteTeamsRepository
            )
        }
        // Inicializador para TeamMatchesViewModel
        initializer {
            TeamMatchesViewModel(
                favoriteTeamsRepository = notiGoalApplication().container.favoriteTeamsRepository,
                userPreferencesRepository = notiGoalApplication().container.userPreferencesRepository // Añadido
            )
        }
        // Inicializador para MatchDetailViewModel (que también necesita el SavedStateHandle)
        initializer {
            MatchDetailViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                favoriteTeamsRepository = notiGoalApplication().container.favoriteTeamsRepository
            )
        }
        // Nuevo inicializador para TeamSelectionViewModel
        initializer {
            TeamSelectionViewModel(
                favoriteTeamsRepository = notiGoalApplication().container.favoriteTeamsRepository
            )
        }
        // Nuevo inicializador para EditProfileViewModel
        initializer {
            EditProfileViewModel(
                userPreferencesRepository = notiGoalApplication().container.userPreferencesRepository
            )
        }
    }
}

/**
 * Función helper para obtener la instancia de la Application desde la fábrica.
 */
fun CreationExtras.notiGoalApplication(): NotiGoalApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NotiGoalApplication)
