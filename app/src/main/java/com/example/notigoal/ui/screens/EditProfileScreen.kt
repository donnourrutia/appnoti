package com.example.notigoal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.* // Importa Material3 para Scaffold, TopAppBar, Icon, etc.
import androidx.compose.runtime.* 
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.notigoal.di.AppViewModelProvider // Importar AppViewModelProvider
import com.example.notigoal.ui.viewmodel.EditProfileViewModel

/**
 * La pantalla de formulario para editar el perfil del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: EditProfileViewModel = viewModel(factory = AppViewModelProvider.Factory) //
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()


    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.userMessageShown()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Boton para volver atras
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            ValidatedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = "Nombre de Usuario",
                isError = uiState.isNameError,
                errorMessage = "El nombre no puede estar vacío"
            )

            ValidatedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = "Correo Electrónico",
                isError = uiState.isEmailError,
                errorMessage = "El formato del correo no es válido"
            )

            OutlinedTextField(
                value = uiState.biography,
                onValueChange = viewModel::onBiographyChange,
                label = { Text("Biografía") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = viewModel::saveProfile,
                enabled = uiState.isFormValid,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("GUARDAR CAMBIOS")
            }
        }
    }
}

/**
 * Un Composable 'helper' para nuestros campos de texto con validación.
 */
@Composable
private fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    )
}