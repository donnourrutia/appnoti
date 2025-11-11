package com.example.notigoal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Patterns // Para validar el formato del email
import com.example.notigoal.data.preferences.UserPreferencesRepository // Nueva importación

class EditProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository // Inyectamos el repositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        // Cargar los datos del perfil al inicializar el ViewModel
        viewModelScope.launch {
            userPreferencesRepository.userProfileFlow.collect { userProfile ->
                _uiState.update { currentState ->
                    currentState.copy(
                        name = userProfile.name,
                        email = userProfile.email,
                        biography = userProfile.biography
                    ).validateForm() // Volver a validar el formulario con los datos cargados
                }
            }
        }
    }

    // Función para actualizar el nombre
    fun onNameChange(newName: String) {
        _uiState.update { currentState ->
            val isNameError = newName.isBlank()
            currentState.copy(name = newName, isNameError = isNameError).validateForm()
        }
    }

    // Función para actualizar el email
    fun onEmailChange(newEmail: String) {
        _uiState.update { currentState ->
            val isEmailError = !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches() && newEmail.isNotBlank()
            currentState.copy(email = newEmail, isEmailError = isEmailError).validateForm()
        }
    }

    // Función para actualizar la biografía
    fun onBiographyChange(newBiography: String) {
        _uiState.update { currentState ->
            currentState.copy(biography = newBiography)
        }
    }

    // Función para guardar el perfil
    fun saveProfile() {
        viewModelScope.launch {
            val currentProfile = _uiState.value
            if (currentProfile.isFormValid) {
                userPreferencesRepository.saveProfile(
                    name = currentProfile.name,
                    email = currentProfile.email,
                    biography = currentProfile.biography
                )
                _uiState.update { it.copy(userMessage = "Perfil guardado correctamente!") }
            } else {
                _uiState.update { it.copy(userMessage = "Por favor, corrige los errores del formulario.") }
            }
        }
    }

    // Función para indicar que el mensaje del usuario ya se mostró
    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    // Función privada para validar el formulario completo
    private fun EditProfileUiState.validateForm(): EditProfileUiState {
        val isNameValid = !this.isNameError && this.name.isNotBlank()
        val isEmailValid = !this.isEmailError && this.email.isNotBlank()
        // La biografía es opcional, no afecta la validez del formulario si está vacía
        return this.copy(isFormValid = isNameValid && isEmailValid)
    }

    // Estado de la UI para el formulario de edición de perfil
    data class EditProfileUiState(
        val name: String = "", // Ahora se inicializarán desde el repositorio
        val email: String = "", // Ahora se inicializarán desde el repositorio
        val biography: String = "", // Ahora se inicializarán desde el repositorio
        val isNameError: Boolean = false,
        val isEmailError: Boolean = false,
        val isFormValid: Boolean = false, // Asumimos inválido hasta cargar y validar
        val userMessage: String? = null
    )
}
