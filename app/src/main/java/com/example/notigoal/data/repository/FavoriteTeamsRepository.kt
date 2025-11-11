package com.example.notigoal.data.repository

import com.example.notigoal.data.db.FavoriteTeam
import com.example.notigoal.data.db.FavoriteTeamDao
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que gestiona las operaciones de datos para los equipos favoritos.
 * Actúa como una capa de abstracción entre los ViewModels y la fuente de datos (Room).
 */
interface FavoriteTeamsRepository {
    /** Devuelve un Flow con la lista de todos los equipos favoritos. */
    fun getAllFavoriteTeams(): Flow<List<FavoriteTeam>>

    /** Añade un equipo a favoritos. */
    suspend fun addFavorite(team: FavoriteTeam)

    /** Elimina un equipo de favoritos. */
    suspend fun removeFavorite(team: FavoriteTeam)

    /** Comprueba si un equipo ya es favorito. */
    suspend fun isFavorite(teamId: Int): Boolean
}

/**
 * Implementación del repositorio que usa Room como fuente de datos.
 */
class CachingFavoriteTeamsRepository(private val favoriteTeamDao: FavoriteTeamDao) : FavoriteTeamsRepository {
    override fun getAllFavoriteTeams(): Flow<List<FavoriteTeam>> {
        return favoriteTeamDao.getAll()
    }

    override suspend fun addFavorite(team: FavoriteTeam) {
        favoriteTeamDao.insert(team)
    }

    override suspend fun removeFavorite(team: FavoriteTeam) {
        favoriteTeamDao.delete(team)
    }

    override suspend fun isFavorite(teamId: Int): Boolean {
        return favoriteTeamDao.getTeamById(teamId) != null
    }
}
