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
    val userPreferencesRepository: UserPreferencesRepository // Nueva propiedad
}

/**
 * Implementación predeterminada de [AppContainer].
 * Proporciona instancias concretas de las dependencias.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    // Instancia de la base de datos de Room
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context = context)
    }

    // Implementación perezosa del repositorio de equipos favoritos
    override val favoriteTeamsRepository: FavoriteTeamsRepository by lazy {
        CachingFavoriteTeamsRepository(database.favoriteTeamDao())
    }

    // Implementación perezosa de la API de fútbol
    override val footballApi: FootballApi by lazy {
        RetrofitInstance.api
    }

    // Nueva implementación perezosa del repositorio de preferencias de usuario
    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }
}
