package com.example.notigoal.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. Definimos nuestra paleta de colores oscuros
private val DarkColorScheme = darkColorScheme(
    primary = PurpleBrand,             // Tu morado como color principal
    background = DarkBackground,       // El fondo de la app será gris oscuro
    surface = DarkSurface,             // El fondo de las tarjetas será un poco más claro
    onPrimary = Color.White,           // Texto sobre un fondo morado (será blanco)
    onBackground = TextPrimaryDark,    // Texto principal sobre el fondo de la app
    onSurface = TextPrimaryDark        // Texto principal sobre las tarjetas
)

@Composable
fun NotiGoalTheme(
    // 2. Forzamos el modo oscuro poniendo 'darkTheme = true'
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // 3. Usamos siempre nuestra paleta oscura

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 4. Hacemos que la barra de estado (arriba) también sea oscura
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // La tipografía que ya tenías
        content = content
    )
}
