package com.example.panaderia.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panaderia.data.model.User
import com.example.panaderia.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState(isAuthenticated = repo.isAuthenticated(), user = repo.getCurrentUser()))
    val state: StateFlow<AuthState> = _state

    fun register(email: String, password: String, name: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val res = repo.register(email.trim(), password, name.trim())
            if (res.isSuccess) {
                val user = res.getOrNull()
                _state.value = AuthState(isLoading = false, isAuthenticated = true, user = user)
                onResult(true, null)
            } else {
                val msg = res.exceptionOrNull()?.localizedMessage ?: "Error en registro"
                _state.value = _state.value.copy(isLoading = false, error = msg)
                onResult(false, msg)
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val res = repo.login(email.trim(), password)
            if (res.isSuccess) {
                val user = res.getOrNull()
                _state.value = AuthState(isLoading = false, isAuthenticated = true, user = user)
                onResult(true, null)
            } else {
                val msg = res.exceptionOrNull()?.localizedMessage ?: "Error en login"
                _state.value = _state.value.copy(isLoading = false, error = msg)
                onResult(false, msg)
            }
        }
    }

    fun logout() {
        repo.logout()
        _state.value = AuthState(isLoading = false, isAuthenticated = false, user = null)
    }
}
