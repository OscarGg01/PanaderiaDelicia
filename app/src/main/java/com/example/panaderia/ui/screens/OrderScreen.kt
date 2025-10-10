package com.example.panaderia.ui.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaderia.ui.viewmodel.OrderViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.panaderia.data.repository.CartEntry
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat

private const val CHANNEL_ID = "panaderia_orders_channel"
private const val NOTIF_ID_ORDER_PLACED = 1001

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    navController: androidx.navigation.NavHostController,
    orderVm: OrderViewModel = hiltViewModel()
) {
    val orderState by orderVm.lastOrder.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
    }

    LaunchedEffect(orderState) {
        val order = orderState
        if (order != null) {
            scope.launch {
                snackbarHostState.showSnackbar("Pedido registrado con éxito")
            }
        }
    }

    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("Boleta de pedido") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
                .padding(padding)
        ) {
            val order = orderState
            if (order == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay pedido reciente")
                }
                return@Column
            }

            Text("Pedido #${order.id}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(order.timestamp))}")
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(order.items) { entry: CartEntry ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            entry.product.name,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text("x${entry.quantity}")
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("S/ ${"%.2f".format(entry.product.price * entry.quantity)}")
                    }
                    Divider()
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Total:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text("S/ ${"%.2f".format(order.total)}", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                val currentOrder = orderState ?: return@Button

                fun showNotification() {
                    createNotificationChannelIfNeeded(ctx)
                    val notif = NotificationCompat.Builder(ctx, CHANNEL_ID)
                        .setSmallIcon(com.example.panaderia.R.mipmap.ic_launcher) // cambia si deseas otro icono
                        .setContentTitle("Pedido registrado")
                        .setContentText("Tu pedido #${currentOrder.id} fue registrado con éxito.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build()
                    NotificationManagerCompat.from(ctx).notify(NOTIF_ID_ORDER_PLACED, notif)
                }

                fun doNotifyAndNavigate(showNotif: Boolean) {
                    if (showNotif) showNotification()
                    orderVm.clearLastOrder()
                    navController.navigate(com.example.panaderia.ui.navigation.Routes.HOME) {
                        launchSingleTop = true
                        popUpTo(com.example.panaderia.ui.navigation.Routes.HOME) { inclusive = false }
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val granted = ContextCompat.checkSelfPermission(
                        ctx,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (granted) {
                        doNotifyAndNavigate(showNotif = true)
                    } else {
                        var handled = false
                        val launcher = notifPermissionLauncher
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)

                        scope.launch {
                            kotlinx.coroutines.delay(300)
                            val nowGranted = ContextCompat.checkSelfPermission(
                                ctx,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                            if (nowGranted) {
                                doNotifyAndNavigate(showNotif = true)
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Permiso de notificaciones denegado. Volviendo a inicio.")
                                }
                                doNotifyAndNavigate(showNotif = false)
                            }
                        }
                    }
                } else {
                    doNotifyAndNavigate(showNotif = true)
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Finalizar")
            }
        }
    }
}

private fun createNotificationChannelIfNeeded(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Pedidos"
        val descriptionText = "Notificaciones de pedidos registrados"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
