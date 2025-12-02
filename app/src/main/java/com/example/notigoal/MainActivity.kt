package com.example.notigoal

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.accompanist.permissions.*
import com.example.notigoal.data.model.*
import com.example.notigoal.di.AppViewModelProvider
import com.example.notigoal.ui.navigation.Screen
import com.example.notigoal.ui.screens.EditProfileScreen
import com.example.notigoal.ui.screens.TeamSelectionScreen
import com.example.notigoal.ui.screens.FeedbackScreen
import com.example.notigoal.ui.theme.LiveGreen
import com.example.notigoal.ui.theme.NotiGoalTheme
import com.example.notigoal.ui.viewmodel.*
import com.example.notigoal.util.NotificationHelper
import com.example.notigoal.data.db.FavoriteTeam
import com.example.notigoal.data.preferences.UserProfile

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.Instant


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)
        val initialMatchId = intent.getIntExtra("matchId", -1)

        setContent {
            NotiGoalTheme {
                AppScreen(initialMatchId = initialMatchId)
            }
        }
    }
}

// Estructura Principal
@Composable
fun AppScreen(initialMatchId: Int = -1) {
    val navController = rememberNavController()
    // Añadir Feedback a la lista de pantallas (aunque no está en el nav bar)
    val items = listOf(Screen.Partidos, Screen.Champions, Screen.Perfil, Screen.TeamSelection, Screen.EditProfile)

    val sharedMatchesViewModel: MatchesViewModel = viewModel(factory = AppViewModelProvider.Factory)

    LaunchedEffect(key1 = initialMatchId) {
        if (initialMatchId != -1) {
            navController.navigate("partido_detalle/$initialMatchId") {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.filter { it.icon != null }.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Partidos.route, Modifier.padding(innerPadding)) {
            composable(Screen.Partidos.route) {
                // Usamos el ViewModel compartido
                val uiState by sharedMatchesViewModel.uiState.collectAsState()
                MatchesScreenWithTabs(uiState = uiState, title = "Partidos", navController = navController)
            }
            composable(Screen.Champions.route) {
                // Champions tiene su propio VM porque es específico
                val viewModel: ChampionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
                val uiState by viewModel.uiState.collectAsState()
                MatchesScreenWithTabs(uiState = uiState, title = "Champions League", navController = navController)
            }
            composable(Screen.Perfil.route) {
                val profileViewModel: TeamMatchesViewModel = viewModel(factory = AppViewModelProvider.Factory)
                val favoriteTeams by profileViewModel.favoriteTeams.collectAsState()
                val userProfile by profileViewModel.userProfile.collectAsState()

                // Usamos el ViewModel compartido
                val mainMatchesUiState by sharedMatchesViewModel.uiState.collectAsState()

                ProfileScreen(
                    navController = navController,
                    favoriteTeams = favoriteTeams,
                    userProfile = userProfile,
                    mainMatchesUiState = mainMatchesUiState
                )
            }
            composable(
                route = "partido_detalle/{matchId}",
                arguments = listOf(navArgument("matchId") { type = NavType.IntType })
            ) {
                val viewModel: MatchDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
                val uiState by viewModel.uiState.collectAsState()
                MatchDetailScreen(navController = navController)
            }
            composable(Screen.TeamSelection.route) {
                TeamSelectionScreen(navController = navController)
            }
            composable(Screen.EditProfile.route) {
                EditProfileScreen(navController = navController)
            }
            composable("feedback") {
                FeedbackScreen(navController = navController)
            }
        }
    }
}


// Pantallas Principales
@Composable
fun MatchesScreenWithTabs(uiState: MatchesUiState, title: String, navController: NavHostController) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Próximos", "En Vivo", "Finalizados")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        when (uiState) {
            is MatchesUiState.Loading -> LoadingView()
            is MatchesUiState.Success -> {
                val filteredMatches = when (tabIndex) {
                    0 -> uiState.matches.filter { it.status == "TIMED" || it.status == "SCHEDULED" || it.status == "POSTPONED" || it.status == "SUSPENDED" || it.status == "CANCELED" }
                    1 -> uiState.matches.filter { it.status == "IN_PLAY" || it.status == "PAUSED" }
                    2 -> uiState.matches.filter { it.status == "FINISHED" }
                    else -> emptyList()
                }

                MatchList(
                    matches = filteredMatches,
                    navController = navController
                )
            }
            is MatchesUiState.Error -> ErrorView()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val viewModel: MatchDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Partido") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when (val state = uiState) {
                is MatchDetailUiState.Loading -> LoadingView()
                is MatchDetailUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                    ) {
                        MatchItem(match = state.match)
                        Text(text = "Estadio: ${state.match.venue ?: "No disponible"}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Marcador Simulado: ${state.simulatedHomeScore} - ${state.simulatedAwayScore}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = {
                                viewModel.simulateHomeGoal()
                                NotificationHelper.showGoalNotification(
                                    context = context,
                                    homeTeamName = state.match.homeTeam.name,
                                    awayTeamName = state.match.awayTeam.name,
                                    score = "${state.simulatedHomeScore + 1} - ${state.simulatedAwayScore}",
                                    minute = "${(0..90).random()}'",
                                    matchId = state.match.id
                                )
                            }) {
                                Text("Gol de ${state.match.homeTeam.shortName}")
                            }
                            Button(onClick = {
                                viewModel.simulateAwayGoal()
                                NotificationHelper.showGoalNotification(
                                    context = context,
                                    homeTeamName = state.match.homeTeam.name,
                                    awayTeamName = state.match.awayTeam.name,
                                    score = "${state.simulatedHomeScore} - ${state.simulatedAwayScore + 1}",
                                    minute = "${(0..90).random()}'",
                                    matchId = state.match.id
                                )
                            }) {
                                Text("Gol de ${state.match.awayTeam.shortName}")
                            }
                        }

                        Text(
                            text = "Eventos del partido (próximamente)",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                }
                is MatchDetailUiState.Error -> ErrorView()
            }
        }
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    favoriteTeams: List<FavoriteTeam>,
    userProfile: UserProfile,
    mainMatchesUiState: MatchesUiState
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { ProfileHeader(userProfile = userProfile) }
        item { FollowingSection(navController = navController, favoriteTeams = favoriteTeams) }
        item {
            TeamMatchesSection(
                uiState = mainMatchesUiState,
                favoriteTeams = favoriteTeams
            )
        }
        item { PermissionsSection(navController = navController) }


        item {
            Column {
                Text(text = "Servicios de Comunidad", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    ListItem(
                        headlineContent = { Text("Enviar Comentarios") },
                        leadingContent = { Icon(Icons.Default.ChatBubble, contentDescription = "Comentarios") },
                        trailingContent = { Icon(Icons.Default.KeyboardArrowRight, contentDescription = null) },
                        modifier = Modifier.clickable { navController.navigate("feedback") } // Ruta a la pantalla de Feedback
                    )
                }
            }
        }

    }
}


// Componentes de UI
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchList(matches: List<Match>, modifier: Modifier = Modifier, title: String? = null, navController: NavHostController) {
    var expandedCompetitions by remember { mutableStateOf(setOf<String>()) }
    val defaultMatchLimit = 3

    val groupedMatches = matches.groupBy { it.competition.name }

    val isCollapsible = groupedMatches.size > 1

    Column(modifier = modifier.fillMaxSize()) {
        if (title != null) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (groupedMatches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No hay partidos para esta categoría.",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        else {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)) {

                groupedMatches.forEach { (competitionName, matchesInCompetition) ->

                    val isExpanded = competitionName in expandedCompetitions || !isCollapsible
                    val hasMoreMatches = matchesInCompetition.size > defaultMatchLimit

                    stickyHeader {
                        CompetitionHeader(
                            name = competitionName,
                            isExpanded = isExpanded,
                            showExpandIcon = hasMoreMatches && isCollapsible,
                            onClick = {
                                if (hasMoreMatches && isCollapsible) {
                                    expandedCompetitions = if (isExpanded) {
                                        expandedCompetitions - competitionName
                                    } else {
                                        expandedCompetitions + competitionName
                                    }
                                }
                            }
                        )
                    }

                    val matchesToDisplay = if (isExpanded) {
                        matchesInCompetition
                    } else {
                        matchesInCompetition.take(defaultMatchLimit)
                    }

                    items(matchesToDisplay) { match ->
                        MatchItem(
                            match = match,
                            modifier = Modifier.clickable {
                                navController.navigate("partido_detalle/${match.id}")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MatchItem(match: Match, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamColumn(team = match.homeTeam, modifier = Modifier.weight(2.0f))
            ScoreColumn(match = match, modifier = Modifier.weight(1.5f))
            TeamColumn(team = match.awayTeam, modifier = Modifier.weight(2.5f), isHomeTeam = false)
        }
    }
}

@Composable
fun ScoreColumn(match: Match, modifier: Modifier = Modifier) {
    val score = match.score
    val status = match.status
    val utcDate = match.utcDate

    val homeScore = score?.fullTime?.home
    val awayScore = score?.fullTime?.away

    val scoreText = if (homeScore == null) {
        formatUtcDate(utcDate)
    } else {
        "$homeScore - $awayScore"
    }

    val statusText = when (status) {
        "FINISHED" -> "Final"
        "IN_PLAY" -> "En Vivo"
        "PAUSED" -> "Descanso"
        "TIMED", "SCHEDULED" -> "Próximo"
        else -> status.replaceFirstChar { it.titlecase() }
    }

    val statusColor = when (status) {
        "IN_PLAY" -> LiveGreen
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = scoreText, fontSize = 22.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = statusText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
    }
}

@Composable
fun TeamColumn(team: Team, modifier: Modifier = Modifier, isHomeTeam: Boolean = true) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isHomeTeam) Alignment.Start else Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(team.crest).decoderFactory(SvgDecoder.Factory()).crossfade(true).build(),
            contentDescription = "Logo ${team.name}",
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Fit,
            loading = { CircularProgressIndicator(modifier = Modifier.size(20.dp)) },
            error = { Text("?", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = team.shortName, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = if (isHomeTeam) TextAlign.Start else TextAlign.End)
    }
}

@Composable
fun CompetitionHeader(
    name: String,
    isExpanded: Boolean,
    showExpandIcon: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        if (showExpandIcon) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Ver menos" else "Ver más",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
        }
    }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

@Composable
fun ErrorView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Error al cargar los partidos.\nRevisa tu conexión o la API Key.", color = Color.Red, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
    }
}

// parámetro modifier
@Composable
fun PlaceholderScreen(screenTitle: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla de $screenTitle", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

// Componentes para el Perfil
@Composable
fun TeamMatchesSection(uiState: MatchesUiState, favoriteTeams: List<FavoriteTeam>) {
    Column {
        Text("Próximos Partidos de tus Equipos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(12.dp))
        when (uiState) {
            is MatchesUiState.Loading -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
            is MatchesUiState.Success -> {
                val favoriteTeamIds = favoriteTeams.map { it.id }.toSet()
                val favoriteMatches = uiState.matches.filter {
                    it.homeTeam.id in favoriteTeamIds || it.awayTeam.id in favoriteTeamIds
                }
                val upcomingFavoriteMatches = favoriteMatches.filter {
                    it.status == "TIMED" || it.status == "SCHEDULED"
                }

                if (upcomingFavoriteMatches.isEmpty()) {
                    Text("No hay próximos partidos programados para tus equipos.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        upcomingFavoriteMatches.take(3).forEach { match -> SmallMatchItem(match) }
                    }
                }
            }
            is MatchesUiState.Error -> {
                Text("Error al cargar los partidos del equipo.")
            }
        }
    }
}

@Composable
fun SmallMatchItem(match: Match) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(match.homeTeam.shortName, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
            Text(formatUtcDate(match.utcDate), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(match.awayTeam.shortName, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsSection(navController: NavHostController) {
    Column {
        Text(text = "Ajustes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            NotificationPermissionHandler()
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            CameraPermissionHandler()
            ListItem(
                headlineContent = { Text("Editar Perfil") },
                leadingContent = { Icon(Icons.Default.Edit, contentDescription = "Editar Perfil") },
                trailingContent = { Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Ir a editar perfil") },
                modifier = Modifier.clickable { navController.navigate(Screen.EditProfile.route) }
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionHandler() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        PermissionRow(
            permissionState = permissionState,
            icon = Icons.Default.Notifications,
            title = "Notificaciones",
            description = "Recibe alertas de goles y noticias",
            rationale = "El permiso fue denestado. Púlsalo para volver a pedirlo."
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionHandler() {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    PermissionRow(
        permissionState = permissionState,
        icon = Icons.Default.CameraAlt,
        title = "Cámara",
        description = "Accede a la cámara para personalizar tu perfil",
        rationale = "El permiso fue denestado. Necesitamos acceso a la cámara."
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRow(permissionState: com.google.accompanist.permissions.PermissionState, icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String, rationale: String) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold)
                val statusText = when {
                    permissionState.status.isGranted -> "Permiso concedido"
                    permissionState.status.shouldShowRationale -> rationale
                    else -> description
                }
                Text(text = statusText, style = MaterialTheme.typography.bodySmall)
            }
        }

        if (permissionState.status.isGranted) {
            Button(onClick = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }) {
                Text("Ajustes")
            }
        } else {
            Button(onClick = { permissionState.launchPermissionRequest() }) {
                Text("Activar")
            }
        }
    }
}

@Composable
fun ProfileHeader(userProfile: UserProfile) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, modifier = Modifier.size(64.dp), color = MaterialTheme.colorScheme.surface) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Icono de perfil", modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = userProfile.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = userProfile.biography, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun FollowingSection(navController: NavHostController, favoriteTeams: List<FavoriteTeam>) {
    Column {
        Text(text = "Siguiendo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { navController.navigate(Screen.TeamSelection.route) },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Seleccionar equipos favoritos")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (favoriteTeams.isEmpty()) {
            Text("Aún no sigues a ningún equipo. Selecciona tus favoritos.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(favoriteTeams) { team ->
                    FavoriteTeamLogo(team = team)
                }
            }
        }
    }
}

@Composable
fun FavoriteTeamLogo(team: FavoriteTeam) {
    Surface(shape = CircleShape, modifier = Modifier.size(56.dp), color = MaterialTheme.colorScheme.surface) {
        Box(contentAlignment = Alignment.Center) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(team.crestUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .crossfade(true)
                    .build(),
                contentDescription = "Logo ${team.name}",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit,
                loading = { CircularProgressIndicator(modifier = Modifier.size(20.dp)) },
                error = { Text(team.shortName, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) }
            )
        }
    }
}

// Funciones Helper y Previews
private fun formatUtcDate(utcDate: String?): String {
    if (utcDate.isNullOrBlank()) {
        Log.w("formatUtcDate", "La fecha UTC es nula o vacía.")
        return "VS"
    }

    return try {
        val instant = Instant.parse(utcDate)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
        zonedDateTime.format(outputFormatter)

    } catch (e: DateTimeParseException) {
        Log.e("formatUtcDate", "Error de formato parseando la fecha: $utcDate", e)
        "Error"
    } catch (e: Exception) {
        Log.e("formatUtcDate", "Error inesperado parseando la fecha: $utcDate", e)
        "Error"
    }
}

@Preview(showBackground = true, name = "AppScreen Preview")
@Composable
fun AppScreenPreview() { NotiGoalTheme { AppScreen() } }

@Preview(showBackground = true, name = "Match Item Scheduled")
@Composable
fun MatchItemPreview() {
    val previewMatch = Match(
        id = 1,
        competition = Competition(0, "LaLiga EA Sports", "", ""),
        utcDate = "2025-11-09T20:00:00Z",
        homeTeam = Team(id = 10, name = "Real Madrid CF", crest = "", shortName = "RMD"),
        awayTeam = Team(id = 11, name = "FC Barcelona", crest = "", shortName = "FCB"),
        score = Score(fullTime = TimeScore(home = null, away = null)),
        status = "SCHEDULED",
        venue = "Santiago Bernabéu"
    )
    NotiGoalTheme { MatchItem(match = previewMatch) }
}

@Preview(showBackground = true, name = "Profile Screen Preview")
@Composable
fun ProfileScreenPreview() {
    NotiGoalTheme {
        ProfileScreen(
            navController = rememberNavController(),
            favoriteTeams = emptyList(),
            userProfile = UserProfile(name = "Usuario", biography = "Bio", email = "usuario@ejemplo.com"),
            mainMatchesUiState = MatchesUiState.Loading
        )
    }
}