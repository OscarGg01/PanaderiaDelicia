package com.example.panaderia.data.repository

import com.example.panaderia.data.db.OrderDao
import com.example.panaderia.data.db.OrderEntity
import com.example.panaderia.data.db.OrderItemEntity
import com.example.panaderia.data.db.OrderWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import com.example.panaderia.data.repository.CartEntry




@Singleton
class OrderRepository @Inject constructor(
    private val dao: OrderDao
) {
    suspend fun saveOrder(orderId: Long, items: List<CartEntry>, total: Double, timestamp: Long) {
        withContext(Dispatchers.IO) {
            val orderEntity = OrderEntity(id = orderId, total = total, timestamp = timestamp)
            val itemEntities = items.map { entry ->
                OrderItemEntity(
                    orderId = orderId,
                    productId = entry.product.id,
                    productName = entry.product.name,
                    unitPrice = entry.product.price,
                    quantity = entry.quantity
                )
            }
            dao.insertOrderWithItems(orderEntity, itemEntities)
        }
    }

    fun observeOrders(): Flow<List<OrderWithItems>> = dao.getOrdersWithItemsFlow()

    suspend fun getOrder(orderId: Long): OrderWithItems? = withContext(Dispatchers.IO) {
        dao.getOrdersWithItemsFlow()
            .map { list -> list.firstOrNull { it.order.id == orderId } }
            .first()
    }
}
