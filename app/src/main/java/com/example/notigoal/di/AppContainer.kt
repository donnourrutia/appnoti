package com.example.notigoal.di

import android.content.Context
import com.example.notigoal.data.db.AppDatabase
import com.example.notigoal.data.remote.FootballApi
import com.example.notigoal.data.remote.RetrofitInstance
import com.example.notigoal.data.repository.CachingFavoriteTeamsRepository
import com.example.notigoal.data.repository.FavoriteTeamsRepository
import com.example.notigoal.data.preferences.UserPreferencesRepository // Nueva importación

/**
 * Interfaz para el contenedor de dependencias de la aplicación.
 * Define las dependencias que estarán disponibles en toda la aplicación.
 */
interface AppContainer {
    val favoriteTeamsRepository: FavoriteTeamsRepository
    val footballApi: FootballApi
    val userPreferencesRepository: UserPreferencesRepository
}

/**
 * Implementación predeterminada de [AppContainer].
 * Proporciona instancias concretas de las dependencias.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context = context)
    }

    override val favoriteTeamsRepository: FavoriteTeamsRepository by lazy {
        CachingFavoriteTeamsRepository(database.favoriteTeamDao())
    }

    override val footballApi: FootballApi by lazy {
        RetrofitInstance.api
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }
}
