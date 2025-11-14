package com.example.notigoal.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * La clase principal de la base de datos para la aplicación.
 * La anotación @Database le dice a Room que esta es una base de datos.
 *
 * entities = El array de todas las "tablas" (Entidades) que usará la base de datos.
 */
@Database(entities = [FavoriteTeam::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTeamDao(): FavoriteTeamDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notigoal_database" 
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}
