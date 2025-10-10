package com.example.panaderia.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OrderEntity::class, OrderItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}
