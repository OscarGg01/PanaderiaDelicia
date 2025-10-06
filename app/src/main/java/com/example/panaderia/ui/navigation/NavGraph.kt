package com.example.panaderia.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.panaderia.ui.screens.HomeScreen
import com.example.panaderia.ui.screens.SplashScreen
import com.example.panaderia.ui.screens.LoginScreen
import com.example.panaderia.ui.screens.RegisterScreen
import com.example.panaderia.ui.screens.ProfileScreen
import com.example.panaderia.ui.screens.CartScreen
import com.example.panaderia.ui.viewmodel.HomeViewModel
import com.example.panaderia.ui.viewmodel.AuthViewModel
import com.example.panaderia.ui.viewmodel.CartViewModel
import androidx.navigation.NavHostController

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"
    const val CART = "cart"
}

@Composable
fun BakeryNavGraph() {
    val navController = rememberNavController()

    // Instancias compartidas
    val authVm: AuthViewModel = hiltViewModel()
    val cartVm: CartViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(onTimeout = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.HOME) {
            val homeVm: HomeViewModel = hiltViewModel()
            val uiState by homeVm.uiState.collectAsState()
            val authState by authVm.state.collectAsState()

            // recolectar items del carrito y sumar cantidades
            val cartItems by cartVm.itemsState.collectAsState()
            val cartCount = cartItems.sumOf { it.quantity }

            HomeScreen(
                uiState = uiState,
                onAddToCart = { product -> homeVm.addToCart(product) },
                navController = navController,
                authState = authState,
                cartCount = cartCount // <-- nuevo parámetro
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                navController,
                { navController.navigateUp() },
                authVm
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                navController,
                { navController.navigateUp() },
                authVm
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                navController,
                authVm
            )
        }

        composable(Routes.CART) {
            CartScreen(navController)
        }
    }
}
