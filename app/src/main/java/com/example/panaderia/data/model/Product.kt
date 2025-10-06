package com.example.panaderia.data.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String? = null,
    val imageRes: Int? = null,
    val stock: Int
)
