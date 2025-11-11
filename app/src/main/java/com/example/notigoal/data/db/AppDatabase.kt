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

    // La base de datos necesita saber sobre el DAO.
    abstract fun favoriteTeamDao(): FavoriteTeamDao

    // Usamos un 'Companion Object' para crear una única instancia de la base de datos
    // (patrón Singleton). Esto evita tener múltiples conexiones abiertas.
    companion object {
        // La anotación @Volatile asegura que el valor de INSTANCE sea siempre
        // el más actualizado y visible para todos los hilos.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si la instancia no es nula, la devolvemos.
            // Si es nula, creamos la base de datos.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notigoal_database" // Nombre del archivo de la base de datos
                ).build()
                INSTANCE = instance
                // Devolvemos la instancia
                instance
            }
        }
    }
}
