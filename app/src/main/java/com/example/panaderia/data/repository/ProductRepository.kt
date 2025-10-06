package com.example.panaderia.data.repository

import com.example.panaderia.data.model.Product

interface ProductRepository {
    suspend fun getProducts(): List<Product>
}
