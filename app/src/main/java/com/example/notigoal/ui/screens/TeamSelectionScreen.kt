package com.example.notigoal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.notigoal.data.model.Team
import com.example.notigoal.di.AppViewModelProvider
import com.example.notigoal.ui.viewmodel.TeamSelectionUiState
import com.example.notigoal.ui.viewmodel.TeamSelectionViewModel

// 1. VERSIÓN PÚBLICA (Para tu Navegación / App Real)
// Esta mantiene la firma original para no romper tu NavHost
@Composable
fun TeamSelectionScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Aquí instanciamos el ViewModel REAL y definimos la navegación REAL
    TeamSelectionScreen(
        viewModel = viewModel(factory = AppViewModelProvider.Factory),
        onBackClick = { navController.navigateUp() },
        onTeamSelected = { /* Aquí podrías navegar al detalle si quisieras */ },
        modifier = modifier
    )
}

// 2. VERSIÓN TESTEABLE (La que usa tu Test)
// Esta recibe el ViewModel y las acciones como parámetros.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamSelectionScreen(
    viewModel: TeamSelectionViewModel, // Inyectado
    onBackClick: () -> Unit,           // Callback en lugar de NavController
    onTeamSelected: (Team) -> Unit,    // Callback
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Equipo Favorito") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { // Usamos el callback
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is TeamSelectionUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) { CircularProgressIndicator() }
                }
                is TeamSelectionUiState.Success -> {
                    if (state.teams.isEmpty()) {
                        Text("No se encontraron equipos.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.teams) { team ->
                                TeamItem(
                                    team = team,
                                    isFavorite = state.favoriteTeamIds.contains(team.id),
                                    onToggleFavorite = { isFavorite ->
                                        viewModel.toggleFavoriteTeam(team, isFavorite)
                                    }
                                )
                            }
                        }
                    }
                }
                is TeamSelectionUiState.Error -> {
                    Text("Error al cargar los equipos.", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun TeamItem(team: Team, isFavorite: Boolean, onToggleFavorite: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleFavorite(isFavorite) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(team.crest)
                    .decoderFactory(SvgDecoder.Factory())
                    .crossfade(true)
                    .build(),
                contentDescription = "Logo ${team.name}",
                modifier = Modifier.size(40.dp),
                loading = { CircularProgressIndicator(modifier = Modifier.size(20.dp)) },
                error = { Text("", modifier = Modifier.size(40.dp)) }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = team.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }

        IconButton(onClick = { onToggleFavorite(isFavorite) }) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}