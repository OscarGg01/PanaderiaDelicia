package com.example.panaderia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaderia.data.model.Product
import com.example.panaderia.data.repository.ProductRepository
import com.example.panaderia.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val products: List<Product> = emptyList(),
    val cart: Map<Int, Int> = emptyMap() // todavía por compatibilidad, pero ya usamos CartRepository
) {
    val cartCount: Int get() = cart.values.sum()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: ProductRepository,
    private val cartRepo: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val prods = repo.getProducts()
            _uiState.value = _uiState.value.copy(isLoading = false, products = prods)
        }
    }

    // now delegate to cartRepo
    fun addToCart(product: Product) {
        cartRepo.addProduct(product)
        // Optionally update local cart snapshot (if you rely on HomeUiState.cart)
        // compute cart map from repo (not necessary). For now leave as is.
    }
}
