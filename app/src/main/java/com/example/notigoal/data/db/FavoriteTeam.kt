package com.example.notigoal.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Esta clase representa la tabla 'favorite_teams' en la base de datos.
 */
@Entity(tableName = "favorite_teams")
data class FavoriteTeam(
    @PrimaryKey val id: Int,
    val name: String,
    val shortName: String,
    val crestUrl: String
)
