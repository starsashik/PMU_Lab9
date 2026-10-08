package com.example.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.repository.AuthRepository
import com.example.app.ui.UiState
import kotlinx.coroutines.launch

/** Хранит состояние авторизации при пересоздании экрана и выполняет запросы в корутинах. */
class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val _state = MutableLiveData<UiState<String>>(UiState.Idle)
    val state: LiveData<UiState<String>> = _state

    /** Запускает вход по почте и паролю; результат публикуется в state. */
    fun login(email: String, password: String) = runRequest {
        repository.login(email.trim(), password)
    }

    /** Запускает регистрацию пользователя; результат публикуется в state. */
    fun register(name: String, email: String, password: String) = runRequest {
        repository.register(name.trim(), email.trim(), password)
    }

    /** Сбрасывает сообщение об ошибке и возвращает форму в исходное состояние. */
    fun reset() {
        _state.value = UiState.Idle
    }

    /** Выполняет запрос в viewModelScope и публикует Loading, Success или Error. */
    private fun runRequest(block: suspend () -> String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                UiState.Success(block())
            } catch (error: Exception) {
                UiState.Error(error.localizedMessage ?: "Не удалось связаться с сервером")
            }
        }
    }
}
