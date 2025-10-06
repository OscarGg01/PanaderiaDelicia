package com.example.panaderia.data.repository

import com.example.panaderia.data.model.Product
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import com.example.panaderia.R

@Singleton
class FakeProductRepository @Inject constructor() : ProductRepository {
    override suspend fun getProducts(): List<Product> {
        delay(500)
        return listOf(
            Product(1, "Pan integral", "Hecho con harina integral y masa madre.", 6.50, imageRes = R.drawable.pan_integral, stock = 10),
            Product(2, "Baguette", "Baguette artesanal, crujiente por fuera.", 3.00, imageRes = R.drawable.baguette, stock = 15),
            Product(3, "Croissant de mantequilla", "Hojaldre y mantequilla real.", 2.50, imageRes = R.drawable.croissant, stock = 12),
            Product(4, "Panettone pequeño", "Con frutas glaseadas.", 12.00, imageRes = R.drawable.panettone, stock = 5)
        )
    }
}
