package com.example.panaderia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.panaderia.ui.components.ProductCard
import com.example.panaderia.ui.viewmodel.HomeUiState
import com.example.panaderia.data.model.Product
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.example.panaderia.ui.viewmodel.AuthState
import com.example.panaderia.ui.navigation.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAddToCart: (Product) -> Unit,
    navController: NavHostController,
    authState: AuthState,
    cartCount: Int // <-- nuevo parámetro
) {
    // Snackbar state y scope
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Panadería DELICIA") },
                actions = {
                    if (authState.isAuthenticated) {
                        IconButton(onClick = { navController.navigate(Routes.PROFILE) }) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil")
                        }
                    } else {
                        TextButton(onClick = { navController.navigate(Routes.LOGIN) }) {
                            Text(text = "Iniciar sesión")
                        }
                    }

                    // Icono carrito con badge — ahora usa cartCount
                    IconButton(onClick = {
                        if (authState.isAuthenticated) {
                            navController.navigate(Routes.CART)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Inicie sesión para ver el carrito")
                            }
                        }
                    }) {
                        BadgedBox(badge = {
                            if (cartCount > 0) {
                                Badge { Text("$cartCount") } // muestra la suma de cantidades
                            }
                        }) {
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
                    }
                }
            )
        },

        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando productos...")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 170.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = uiState.products) { product ->
                        ProductCard(product = product, onAddToCart = onAddToCart)
                    }
                }
            }
        }
    }
}
