package com.example.panaderia.data.repository

import com.example.panaderia.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class CartEntry(
    val product: Product,
    val quantity: Int
)

@Singleton
class CartRepository @Inject constructor() {
    // Internally keep a map productId -> CartEntry
    private val _items = MutableStateFlow<Map<Int, CartEntry>>(emptyMap())
    val items: StateFlow<Map<Int, CartEntry>> = _items.asStateFlow()

    // Add one unit of the product (or create entry)
    fun addProduct(product: Product) {
        val current = _items.value.toMutableMap()
        val existing = current[product.id]
        if (existing == null) {
            current[product.id] = CartEntry(product = product, quantity = 1)
        } else {
            val newQty = (existing.quantity + 1).coerceAtMost(product.stock)
            current[product.id] = existing.copy(quantity = newQty)
        }
        _items.value = current
    }

    // Set explicit quantity (>=0). If qty==0 remove entry.
    fun setQuantity(productId: Int, qty: Int) {
        val current = _items.value.toMutableMap()
        val entry = current[productId]
        if (entry != null) {
            if (qty <= 0) {
                current.remove(productId)
            } else {
                val newQty = qty.coerceAtMost(entry.product.stock)
                current[productId] = entry.copy(quantity = newQty)
            }
            _items.value = current
        }
    }

    // Remove product completely
    fun remove(productId: Int) {
        val current = _items.value.toMutableMap()
        if (current.remove(productId) != null) {
            _items.value = current
        }
    }

    // Clear cart
    fun clear() {
        _items.value = emptyMap()
    }

    // Utility to compute total
    fun totalAmount(): Double {
        return _items.value.values.fold(0.0) { acc, entry ->
            acc + (entry.product.price * entry.quantity)
        }
    }
}
