package com.example.panaderia.ui.screens

import androidx.compose.foundation.clickable
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
import com.example.panaderia.data.db.OrderWithItems
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navController: androidx.navigation.NavHostController,
    orderVm: OrderViewModel = hiltViewModel()
) {
    val orders by orderVm.ordersFlow.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { SmallTopAppBar(title = { Text("Historial de pedidos") }) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
                .padding(padding)
        ) {
            if (orders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay pedidos registrados")
                }
                return@Column
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(orders) { orderWithItems: OrderWithItems ->
                    OrderListRow(orderWithItems = orderWithItems, onClick = {
                    })
                    Divider()
                }
            }
        }
    }
}

@Composable
fun OrderListRow(orderWithItems: OrderWithItems, onClick: () -> Unit) {
    val order = orderWithItems.order
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Pedido #${order.id}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Text("S/ ${"%.2f".format(order.total)}", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text("Fecha: ${sdf.format(Date(order.timestamp))}", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            orderWithItems.items.take(3).forEach { item ->
                Text("- ${item.productName} x${item.quantity}", style = MaterialTheme.typography.bodyMedium)
            }
            if (orderWithItems.items.size > 3) {
                Text("+ ${orderWithItems.items.size - 3} más", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
