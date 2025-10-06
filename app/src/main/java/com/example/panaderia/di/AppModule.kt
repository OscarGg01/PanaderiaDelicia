package com.example.panaderia.di

import android.content.Context
import android.content.SharedPreferences
import com.example.panaderia.data.repository.AuthRepository
import com.example.panaderia.data.repository.SharedPrefAuthRepository
import com.example.panaderia.data.repository.FakeProductRepository
import com.example.panaderia.data.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: FakeProductRepository): ProductRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: SharedPrefAuthRepository): AuthRepository

    companion object {
        @JvmStatic
        @Provides
        @Singleton
        fun provideSharedPreferences(@ApplicationContext ctx: Context): SharedPreferences {
            return ctx.getSharedPreferences("panaderia_prefs", Context.MODE_PRIVATE)
        }
    }
}
