package com.example.notigoal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Patterns
import com.example.notigoal.data.preferences.UserPreferencesRepository

class EditProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {

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

    // Funcion para actualizar el nombre
    fun onNameChange(newName: String) {
        _uiState.update { currentState ->
            val isNameError = newName.isBlank()
            currentState.copy(name = newName, isNameError = isNameError).validateForm()
        }
    }

    // Funcion para actualizar el email
    fun onEmailChange(newEmail: String) {
        _uiState.update { currentState ->
            val isEmailError = !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches() && newEmail.isNotBlank()
            currentState.copy(email = newEmail, isEmailError = isEmailError).validateForm()
        }
    }

    // Funcion para actualizar la biografia
    fun onBiographyChange(newBiography: String) {
        _uiState.update { currentState ->
            currentState.copy(biography = newBiography)
        }
    }

    // Funcion para guardar el perfil
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

    // Funcion para indicar que el mensaje del usuario ya se mostro
    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    // Funcion privada para validar el formulario completo
    private fun EditProfileUiState.validateForm(): EditProfileUiState {
        val isNameValid = !this.isNameError && this.name.isNotBlank()
        val isEmailValid = !this.isEmailError && this.email.isNotBlank()
        // La biografia es opcional, no afecta la validez del formulario si esta vacia
        return this.copy(isFormValid = isNameValid && isEmailValid)
    }

    // Estado de la UI para el formulario de edicion de perfil
    data class EditProfileUiState(
        val name: String = "",
        val email: String = "",
        val biography: String = "",
        val isNameError: Boolean = false,
        val isEmailError: Boolean = false,
        val isFormValid: Boolean = false,
        val userMessage: String? = null
    )
}
