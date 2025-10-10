package com.example.panaderia.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val orderId: Long,
    val productId: Int,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int
)
