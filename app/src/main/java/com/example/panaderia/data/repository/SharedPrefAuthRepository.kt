package com.example.panaderia.data.repository

import android.content.SharedPreferences
import com.example.panaderia.data.model.User
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import android.util.Patterns

@Singleton
class SharedPrefAuthRepository @Inject constructor(
    private val prefs: SharedPreferences
) : AuthRepository {

    private val KEY_USER = "key_user"
    private val KEY_PASSWORD = "key_password"
    private val KEY_AUTH = "key_auth"

    private fun userToJson(user: User): String {
        val obj = JSONObject()
        obj.put("email", user.email)
        obj.put("name", user.name)
        return obj.toString()
    }

    private fun jsonToUser(json: String?): User? {
        if (json.isNullOrBlank()) return null
        val obj = JSONObject(json)
        return User(obj.getString("email"), obj.getString("name"))
    }

    override suspend fun register(email: String, password: String, name: String): Result<User> {
        return withContext(Dispatchers.IO) {
            // Validaciones: email válido, password mínimo, name no vacío
            if (name.isBlank()) {
                return@withContext Result.failure(Exception("Nombre inválido"))
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return@withContext Result.failure(Exception("Correo inválido"))
            }
            if (password.length < 4) {
                return@withContext Result.failure(Exception("Contraseña demasiado corta"))
            }

            // resto del registro (guardado en prefs)
            val user = User(email = email, name = name)
            prefs.edit()
                .putString(KEY_USER, userToJson(user))
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_AUTH, true)
                .apply()
            Result.success(user)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            val storedJson = prefs.getString(KEY_USER, null)
            val storedPassword = prefs.getString(KEY_PASSWORD, null)
            val user = jsonToUser(storedJson)
            if (user == null) return@withContext Result.failure(Exception("Usuario no encontrado"))
            if (user.email == email && storedPassword == password) {
                prefs.edit().putBoolean(KEY_AUTH, true).apply()
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        }
    }

    override fun logout() {
        prefs.edit().putBoolean(KEY_AUTH, false).apply()
    }

    override fun getCurrentUser(): User? {
        val json = prefs.getString(KEY_USER, null)
        return jsonToUser(json)
    }

    override fun isAuthenticated(): Boolean {
        return prefs.getBoolean(KEY_AUTH, false)
    }
}
