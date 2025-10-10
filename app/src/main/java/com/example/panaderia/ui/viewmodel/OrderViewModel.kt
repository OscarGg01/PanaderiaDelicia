package com.example.panaderia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaderia.data.repository.CartEntry
import com.example.panaderia.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val cartRepo: CartRepository
) : ViewModel() {

    private val _lastOrder = MutableStateFlow<Order?>(null)
    val lastOrder: StateFlow<Order?> = _lastOrder
    fun placeOrder(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val itemsMap = cartRepo.items.value
                val itemsList = itemsMap.values.toList()
                val total = itemsList.fold(0.0) { acc, e -> acc + e.product.price * e.quantity }
                val order = Order(
                    id = System.currentTimeMillis(),
                    items = itemsList,
                    total = total,
                    timestamp = System.currentTimeMillis()
                )
                // Guarda orden snapshot
                _lastOrder.value = order
                // Limpia carrito
                cartRepo.clear()
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun clearLastOrder() {
        _lastOrder.value = null
    }
}
