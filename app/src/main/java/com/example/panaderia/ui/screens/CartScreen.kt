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
import com.example.panaderia.ui.viewmodel.OrderViewModel
import com.example.panaderia.ui.navigation.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavHostController,
    orderVm: OrderViewModel = hiltViewModel(), // puedes pasar la instancia desde NavGraph
    vm: CartViewModel = hiltViewModel()
) {
    val items by vm.itemsState.collectAsState()
    val total by vm.totalState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text("Carrito") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
                .padding(padding)
        ) {
            if (items.isEmpty()) {
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

            Button(
                onClick = {
                    orderVm.placeOrder { success ->
                        if (success) {
                            navController.navigate(Routes.ORDER) {
                                launchSingleTop = true
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Error al registrar el pedido. Intenta de nuevo.")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Realizar pedido")
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

            var qtyText by remember { mutableStateOf(entry.quantity.toString()) }

            LaunchedEffect(entry.quantity) {
                qtyText = entry.quantity.toString()
            }

            OutlinedTextField(
                value = qtyText,
                onValueChange = { v ->
                    val filtered = v.filter { it.isDigit() }
                    val safe = if (filtered.isBlank()) "0" else filtered
                    qtyText = safe

                    val intV = safe.toIntOrNull()
                    if (intV != null) {
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
