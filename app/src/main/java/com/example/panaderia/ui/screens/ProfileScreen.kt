package com.example.panaderia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaderia.ui.viewmodel.AuthViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.navigation.NavHostController
import com.example.panaderia.ui.navigation.Routes

@Composable
fun ProfileScreen(
    navController: NavHostController,
    authVm: AuthViewModel = hiltViewModel()
) {
    val state by authVm.state.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Perfil", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Nombre: ${state.user?.name ?: "-"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Correo: ${state.user?.email ?: "-"}")

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = {
                        // Navegar a historial de pedidos
                        navController.navigate(Routes.ORDERS)
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Ver historial")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        authVm.logout()
                        val popped = navController.popBackStack(route = Routes.HOME, inclusive = false)
                        if (!popped) {
                            navController.navigate(Routes.HOME) {
                                launchSingleTop = true
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cerrar sesión")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = { navController.navigateUp() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}
