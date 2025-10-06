package com.example.panaderia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import com.example.panaderia.R

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Usa un drawable/logo en res/drawable/logo.png
        Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Logo")
    }

    LaunchedEffect(Unit) {
        delay(1400) // 1.4s
        onTimeout()
    }
}
