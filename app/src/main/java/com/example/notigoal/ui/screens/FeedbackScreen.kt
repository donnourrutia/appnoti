package com.example.notigoal.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.notigoal.data.remote.BackendRetrofitInstance
import com.example.notigoal.data.remote.Feedback
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var feedbackList by remember { mutableStateOf(emptyList<Feedback>()) }
    var messageText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) } // Para mostrar errores en pantalla

    fun loadFeedbacks() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = BackendRetrofitInstance.api.getFeedbacks()
                if (response.isSuccessful) {
                    feedbackList = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error del servidor: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("FeedbackScreen", "Error cargando comentarios", e)
                errorMessage = "No se pudo conectar al servidor."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadFeedbacks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comentarios de la Comunidad") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Mostrar mensaje de error si existe
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Lista de Comentarios
            if (isLoading && feedbackList.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
                    if (feedbackList.isEmpty()) {
                        item { Text("Aún no hay comentarios. ¡Sé el primero!", modifier = Modifier.padding(8.dp)) }
                    }
                    items(feedbackList) { feedback ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(feedback.userName, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                                Text(feedback.message, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            // Formulario de Envío
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Escribe tu opinión...") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))

                // --- AQUÍ ESTABA EL PROBLEMA ---
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            scope.launch {
                                val newFeedback = Feedback(userName = "Usuario App", message = messageText)
                                // Bloque TRY-CATCH para evitar el CRASH
                                try {
                                    BackendRetrofitInstance.api.sendFeedback(newFeedback)
                                    messageText = "" // Limpiar campo
                                    errorMessage = null
                                    loadFeedbacks() // Recargar
                                } catch (e: Exception) {
                                    Log.e("FeedbackScreen", "Error enviando", e)
                                    errorMessage = "Error al enviar. ¿El servidor está corriendo?"
                                }
                            }
                        }
                    },
                    enabled = messageText.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}