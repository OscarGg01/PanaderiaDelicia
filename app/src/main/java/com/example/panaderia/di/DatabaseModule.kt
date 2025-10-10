package com.example.panaderia.di

import android.content.Context
import androidx.room.Room
import com.example.panaderia.data.db.AppDatabase
import com.example.panaderia.data.db.OrderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    companion object {
        @Provides
        @Singleton
        @JvmStatic
        fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase {
            return Room.databaseBuilder(ctx, AppDatabase::class.java, "panaderia_db")
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        @JvmStatic
        fun provideOrderDao(db: AppDatabase): OrderDao = db.orderDao()
    }
}
