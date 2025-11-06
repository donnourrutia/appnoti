package com.example.notigoal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage // <-- IMPORT CORREGIDO/AÑADIDO
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.notigoal.data.model.Match
import com.example.notigoal.data.model.Score
import com.example.notigoal.data.model.FullTimeScore
import com.example.notigoal.data.model.Team
import com.example.notigoal.ui.theme.NotiGoalTheme
import com.example.notigoal.viewmodel.MatchesUiState
import com.example.notigoal.viewmodel.MatchesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiGoalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Obtenemos la instancia de nuestro ViewModel
                    val viewModel: MatchesViewModel = viewModel()

                    // 2. Observamos el estado (uiState)
                    //    collectAsState() convierte el Flow en un State
                    //    que Compose puede "leer" y reaccionar a sus cambios.
                    val uiState by viewModel.uiState.collectAsState()

                    // 3. Llamamos a nuestra pantalla principal
                    //    pasándole el estado actual.
                    MatchesScreen(uiState = uiState)
                }
            }
        }
    }
}

/**
 * La pantalla principal.
 * Decide qué mostrar basándose en el [uiState].
 */
@Composable
fun MatchesScreen(uiState: MatchesUiState) {
    Scaffold(
        topBar = {
            // Una barra superior simple con el título de la app
            Text(
                text = "NotiGoal - Partidos en Vivo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    ) { paddingValues ->
        // El 'when' (como un 'switch') que maneja los 3 estados
        when (uiState) {
            is MatchesUiState.Loading -> {
                // Estado 1: Cargando
                LoadingView(modifier = Modifier.padding(paddingValues))
            }
            is MatchesUiState.Success -> {
                // Estado 2: Éxito
                // Le pasamos la lista de partidos
                MatchList(
                    matches = uiState.matches,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is MatchesUiState.Error -> {
                // Estado 3: Error
                // Mostramos el mensaje de error
                ErrorView(
                    message = uiState.message,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * Muestra un círculo de carga centrado.
 */
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Muestra un mensaje de error centrado.
 */
@Composable
fun ErrorView(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = Color.Red, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
    }
}

/**
 * Muestra la lista de partidos usando LazyColumn (una lista eficiente).
 */
@Composable
fun MatchList(matches: List<Match>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        // 'items' es el 'forEach' de LazyColumn
        items(matches) { match ->
            MatchItem(match = match)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * El Composable para una sola "fila" de partido.
 * Muestra EquipoLocal vs EquipoVisitante y el marcador.
 */
@Composable
fun MatchItem(match: Match, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna para el Equipo Local
            TeamColumn(team = match.homeTeam, modifier = Modifier.weight(1f))

            // Columna para el Marcador y Estado
            ScoreColumn(
                score = match.score,
                status = match.status
            )

            // Columna para el Equipo Visitante
            TeamColumn(team = match.awayTeam, modifier = Modifier.weight(1f), isHomeTeam = false)
        }
    }
}

/**
 * Muestra el logo (Crest) y el nombre de un equipo.
 * *** ESTA ES LA FUNCIÓN CORREGIDA ***
 */
@Composable
fun TeamColumn(team: Team, modifier: Modifier = Modifier, isHomeTeam: Boolean = true) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isHomeTeam) Alignment.Start else Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        // Usamos SubcomposeAsyncImage para poder mostrar un 'loading' y un 'error'
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(team.crest) // La URL del logo
                .decoderFactory(SvgDecoder.Factory()) // La API de football-data usa SVGs
                .crossfade(true)
                .build(),
            contentDescription = "Logo ${team.name}",
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit, // contentScale se aplica al 'success'
            loading = {
                // Muestra un círculo de carga mientras la imagen baja
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            },
            error = {
                // Esto es lo que queríamos: un fallback en caso de error
                Text("?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = team.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = if (isHomeTeam) TextAlign.Start else TextAlign.End,
            maxLines = 2
        )
    }
}

/**
 * Muestra el marcador (ej: "2 - 1") y el estado (ej: "FINISHED").
 * *** ESTA ES LA FUNCIÓN CORREGIDA ***
 */
@Composable
fun ScoreColumn(score: Score, status: String, modifier: Modifier = Modifier) {
    // Manejamos el caso de que el partido no haya empezado (marcador 'null')
    val homeScore = score.fullTime?.home?.toString() ?: "-"
    val awayScore = score.fullTime?.away?.toString() ?: "-"
    val scoreText = "$homeScore - $awayScore"

    // Mapeamos los estados de la API a algo más legible
    val statusText = when (status) {
        "FINISHED" -> "Finalizado"
        "IN_PLAY" -> "En Vivo"
        "PAUSED" -> "Descanso"
        "TIMED", "SCHEDULED" -> "Programado" // <-- CORRECCIÓN AQUÍ
        else -> status
    }

    val statusColor = when (status) {
        "IN_PLAY" -> Color(0xFF008000) // Verde
        "FINISHED" -> Color.Red
        else -> Color.Gray
    }

    Column(
        modifier = modifier.padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = scoreText,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = statusText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = statusColor
        )
    }
}

// --- Previews (para ver el diseño sin correr la app) ---

@Preview(showBackground = true)
@Composable
fun MatchItemPreview() {
    val previewMatch = Match(
        id = 1,
        homeTeam = Team(id = 10, name = "Equipo Local FC", crest = ""),
        awayTeam = Team(id = 11, name = "Visitante SAD", crest = ""),
        score = Score(fullTime = FullTimeScore(home = 3, away = 1)),
        status = "FINISHED"
    )
    NotiGoalTheme {
        MatchItem(match = previewMatch)
    }
}

@Preview(showBackground = true)
@Composable
fun MatchItemInPlayPreview() {
    val previewMatch = Match(
        id = 1,
        homeTeam = Team(id = 10, name = "Real Madrid", crest = ""),
        awayTeam = Team(id = 11, name = "FC Barcelona", crest = ""),
        score = Score(fullTime = FullTimeScore(home = 0, away = 0)),
        status = "IN_PLAY"
    )
    NotiGoalTheme {
        MatchItem(match = previewMatch)
    }
}