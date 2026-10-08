package com.example.app.ui

/** Общее состояние экрана: ожидание, загрузка, успешные данные или сообщение об ошибке. */
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
