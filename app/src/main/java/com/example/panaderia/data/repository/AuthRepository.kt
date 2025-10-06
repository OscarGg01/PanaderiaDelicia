package com.example.panaderia.data.repository

import com.example.panaderia.data.model.User

interface AuthRepository {
    suspend fun register(email: String, password: String, name: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    fun logout()
    fun getCurrentUser(): User?
    fun isAuthenticated(): Boolean
}
