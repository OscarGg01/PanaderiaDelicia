package com.example.panaderia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaderia.data.repository.CartRepository
import com.example.panaderia.data.repository.OrderRepository
import com.example.panaderia.data.repository.CartEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Order(
    val id: Long,
    val items: List<CartEntry>,
    val total: Double,
    val timestamp: Long
)

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val cartRepo: CartRepository,
    private val orderRepo: OrderRepository
) : ViewModel() {

    private val _lastOrder = MutableStateFlow<Order?>(null)
    val lastOrder: StateFlow<Order?> = _lastOrder

    val ordersFlow = orderRepo.observeOrders().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun placeOrder(onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                val itemsList = cartRepo.items.value.values.toList()
                if (itemsList.isEmpty()) {
                    onResult(false, "El carrito está vacío")
                    return@launch
                }
                val total = itemsList.fold(0.0) { acc, e -> acc + e.product.price * e.quantity }
                val id = System.currentTimeMillis()
                val timestamp = System.currentTimeMillis()

                orderRepo.saveOrder(orderId = id, items = itemsList, total = total, timestamp = timestamp)

                val order = Order(id = id, items = itemsList, total = total, timestamp = timestamp)
                _lastOrder.value = order

                cartRepo.clear()

                onResult(true, null)
            } catch (t: Throwable) {
                onResult(false, t.message ?: "Error desconocido")
            }
        }
    }


    fun clearLastOrder() {
        _lastOrder.value = null
    }
}
