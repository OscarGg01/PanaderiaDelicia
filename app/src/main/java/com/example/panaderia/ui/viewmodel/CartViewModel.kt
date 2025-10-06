package com.example.panaderia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaderia.data.repository.CartRepository
import com.example.panaderia.data.repository.CartEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepo: CartRepository
) : ViewModel() {

    // Lista reactiva de entradas del carrito (StateFlow<List<CartEntry>>)
    val itemsState: StateFlow<List<CartEntry>> =
        cartRepo.items
            .map { it.values.toList() }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Total reactivo (StateFlow<Double>)
    val totalState: StateFlow<Double> =
        cartRepo.items
            .map { m -> m.values.fold(0.0) { acc, e -> acc + e.product.price * e.quantity } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    // helper (sincrónico) si prefieres llamar a una función
    fun total(): Double = cartRepo.totalAmount()

    fun addProduct(product: com.example.panaderia.data.model.Product) {
        cartRepo.addProduct(product)
    }

    fun setQuantity(productId: Int, qty: Int) {
        cartRepo.setQuantity(productId, qty)
    }

    fun remove(productId: Int) {
        cartRepo.remove(productId)
    }

    fun clear() {
        cartRepo.clear()
    }
}
