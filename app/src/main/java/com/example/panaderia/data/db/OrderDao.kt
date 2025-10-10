package com.example.panaderia.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderItems(items: List<OrderItemEntity>): List<Long>

    @Transaction
    fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        insertOrder(order)
        insertOrderItems(items)
    }

    @Transaction
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getOrdersWithItemsFlow(): Flow<List<OrderWithItems>>
}
