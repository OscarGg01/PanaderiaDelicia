package com.example.panaderia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaderia.ui.viewmodel.CartViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.example.panaderia.data.repository.CartEntry
import androidx.navigation.NavHostController
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavHostController,
    vm: CartViewModel = hiltViewModel()
) {
    // Recolectar estado reactivo desde el ViewModel
    val items by vm.itemsState.collectAsState()
    val total by vm.totalState.collectAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text("Carrito") })
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
                .padding(padding)
        ) {
            if (items.isEmpty()) {
                // Carrito vacío: mostrar mensaje y botón Volver
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "El carrito está vacío",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Añade productos desde la pantalla principal.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { navController.navigateUp() },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            Text("Volver")
                        }
                    }
                }
                return@Column
            }

            // Lista de items
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items) { entry: CartEntry ->
                    CartRow(
                        entry = entry,
                        onIncrease = { vm.setQuantity(entry.product.id, entry.quantity + 1) },
                        onDecrease = { vm.setQuantity(entry.product.id, entry.quantity - 1) },
                        onRemove = { vm.remove(entry.product.id) },
                        onSetQuantity = { newQty -> vm.setQuantity(entry.product.id, newQty) }
                    )
                    Divider()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Total y acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "S/ ${"%.2f".format(total)}", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { /* ir a pasarela de pagos más adelante */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Pagar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { navController.navigateUp() }, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}

@Composable
fun CartRow(
    entry: CartEntry,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    onSetQuantity: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen: si existe imageRes usamos painterResource, si no usamos AsyncImage con URL
        Box(modifier = Modifier.size(80.dp)) {
            val ctx = LocalContext.current
            if (entry.product.imageRes != null) {
                Image(
                    painter = painterResource(id = entry.product.imageRes),
                    contentDescription = entry.product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(ctx)
                        .data(entry.product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = entry.product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = com.example.panaderia.R.drawable.placeholder_image),
                    error = painterResource(id = com.example.panaderia.R.drawable.placeholder_image)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                entry.product.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("S/ ${"%.2f".format(entry.product.price)}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Subtotal: S/ ${"%.2f".format(entry.product.price * entry.quantity)}", style = MaterialTheme.typography.bodySmall)
        }

        // Controles de cantidad y eliminar
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = onIncrease) {
                Text("+")
            }

            // qtyText se sincroniza con entry.quantity usando LaunchedEffect
            var qtyText by remember { mutableStateOf(entry.quantity.toString()) }

            // Cuando entry.quantity cambia (por ejemplo por onIncrease desde ViewModel),
            // actualizamos qtyText para que la UI muestre el valor real.
            LaunchedEffect(entry.quantity) {
                qtyText = entry.quantity.toString()
            }

            OutlinedTextField(
                value = qtyText,
                onValueChange = { v ->
                    // mantener solo dígitos (evita que el usuario escriba espacios/letras)
                    val filtered = v.filter { it.isDigit() }
                    qtyText = filtered

                    // si hay un número válido lo pasamos al repo
                    val intV = filtered.toIntOrNull()
                    if (intV != null) {
                        // opcional: podrías limitar intV al stock máximo aquí
                        val limited = intV.coerceIn(0, entry.product.stock)
                        if (limited != entry.quantity) {
                            onSetQuantity(limited)
                        }
                    }
                },
                modifier = Modifier.width(64.dp),
                singleLine = true
            )

            IconButton(onClick = onDecrease) {
                Text("-")
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(onClick = onRemove) {
                Text("Eliminar")
            }
        }
    }
}
