package com.example.notigoal.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Partidos : Screen("partidos", "Partidos", Icons.Default.Home)
    object Champions : Screen("champions", "Champions", Icons.Default.List)
    object Perfil : Screen("perfil", "Perfil", Icons.Default.Person)
}
