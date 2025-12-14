package com.example.notigoal.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Definición simple de los 4 Roles obligatorios
enum class UserRole { ADMIN, USER, GUEST, SUPPORT }

data class User(val email: String, val name: String, val role: UserRole)

class AuthViewModel : ViewModel() {
    // Estado de la UI
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var name by mutableStateOf("")
    var selectedRole by mutableStateOf(UserRole.USER) // Por defecto

    // Estado de carga y errores
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    // "Base de datos" en memoria (Simulación )
    private val registeredUsers = mutableListOf(
        User("admin@notigoal.com", "Admin Principal", UserRole.ADMIN),
        User("user@notigoal.com", "Usuario Normal", UserRole.USER),
        User("guest@notigoal.com", "Invitado", UserRole.GUEST),
        User("support@notigoal.com", "Soporte Técnico", UserRole.SUPPORT)
    )

    fun login() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            delay(1000) // Simular red

            // Lógica simple de validación
            val user = registeredUsers.find { it.email == email }
            if (user != null && password.isNotEmpty()) {
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error("Credenciales inválidas o usuario no encontrado.")
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            delay(1000)

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                val newUser = User(email, name, selectedRole)
                registeredUsers.add(newUser)
                _authState.value = AuthState.Success(newUser)
            } else {
                _authState.value = AuthState.Error("Por favor completa todos los campos.")
            }
        }
    }

    fun recoverPassword() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            delay(1500)
            if (email.isNotEmpty()) {
                // Simulamos envío de correo
                _authState.value = AuthState.PasswordRecovered("Se envió un correo a $email")
            } else {
                _authState.value = AuthState.Error("Ingresa tu correo para recuperar la contraseña.")
            }
        }
    }

    fun logout() {
        _authState.value = AuthState.Idle
        email = ""
        password = ""
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
    data class PasswordRecovered(val message: String) : AuthState()
}