package com.example.notigoal.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la tabla de equipos favoritos.
 * Aquí se definen las operaciones de la base de datos.
 */
@Dao
interface FavoriteTeamDao {

    /**
     * Inserta un equipo en la tabla. Si el equipo ya existe, lo reemplaza.
     * La anotación @Insert se encarga de generar el código SQL necesario.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: FavoriteTeam)

    /**
     * Elimina un equipo de la tabla.
     */
    @Delete
    suspend fun delete(team: FavoriteTeam)

    /**
     * Obtiene todos los equipos favoritos de la tabla, ordenados por nombre.
     * Usamos Flow para que la UI se actualice automáticamente cuando cambien los datos.
     */
    @Query("SELECT * FROM favorite_teams ORDER BY name ASC")
    fun getAll(): Flow<List<FavoriteTeam>>

    /**
     * Busca un equipo por su ID. Devuelve el equipo o null si no se encuentra.
     */
    @Query("SELECT * FROM favorite_teams WHERE id = :teamId")
    suspend fun getTeamById(teamId: Int): FavoriteTeam?
}
