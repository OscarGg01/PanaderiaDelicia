package com.example.panaderia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.panaderia.ui.viewmodel.AuthViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import android.util.Patterns

@Composable
fun RegisterScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    authVm: AuthViewModel
) {
    val state by authVm.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // helper local
    fun isValidEmailLocal(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Crear cuenta")

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    // Validaciones en UI: email válido y requisitos básicos
                    when {
                        name.isBlank() -> scope.launch { snackbarHostState.showSnackbar("Ingresa tu nombre") }
                        !isValidEmailLocal(email) -> scope.launch { snackbarHostState.showSnackbar("Ingresa un correo válido (ejemplo@correo.com)") }
                        password.length < 4 -> scope.launch { snackbarHostState.showSnackbar("La contraseña debe tener al menos 4 caracteres") }
                        else -> {
                            // Llamada al ViewModel / repo (que también valida)
                            authVm.register(email.trim(), password, name.trim()) { success, err ->
                                if (success) {
                                    onBack() // vuelve a Home (authVm comparte estado)
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(err ?: "Error al crear cuenta")
                                    }
                                }
                            }
                        }
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Crear cuenta")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { onBack() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Volver")
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}
