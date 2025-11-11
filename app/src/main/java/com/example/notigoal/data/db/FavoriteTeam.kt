package com.example.notigoal.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Esta clase representa la tabla 'favorite_teams' en la base de datos.
 */
@Entity(tableName = "favorite_teams")
data class FavoriteTeam(
    @PrimaryKey val id: Int, // Usaremos el mismo ID del equipo de la API
    val name: String,
    val shortName: String,
    val crestUrl: String
)
