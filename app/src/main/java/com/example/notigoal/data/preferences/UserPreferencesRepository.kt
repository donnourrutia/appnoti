package com.example.notigoal.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Objeto para representar los datos del perfil de usuario
data class UserProfile(
    val name: String,
    val email: String,
    val biography: String
)

class UserPreferencesRepository(private val context: Context) {

    // Flow para observar los datos del perfil del usuario
    val userProfileFlow: Flow<UserProfile> = context.dataStore.data
        .map { preferences ->
            UserProfile(
                name = preferences[USER_NAME_KEY] ?: "Donnovan",
                email = preferences[USER_EMAIL_KEY] ?: "donnovan@example.com",
                biography = preferences[USER_BIOGRAPHY_KEY] ?: "Apasionado por el fútbol y la tecnología."
            )
        }

    // Suspended function para guardar los datos del perfil
    suspend fun saveProfile(
        name: String,
        email: String,
        biography: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_BIOGRAPHY_KEY] = biography
        }
    }

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_profile_preferences")

        // Keys para almacenar los datos del perfil
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_BIOGRAPHY_KEY = stringPreferencesKey("user_biography")
    }
}
