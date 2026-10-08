package com.example.app.repository

import com.example.app.api.LoginUserRequest
import com.example.app.api.RegisterUserRequest
import com.example.app.api.RetrofitClient

/** Обращается к API авторизации и проверяет ответы сервера. */
class AuthRepository {
    private val api = RetrofitClient.api

    /** Отправляет данные входа и возвращает userId; при отказе сервера выбрасывает исключение. */
    suspend fun login(email: String, password: String): String {
        val response = api.login(LoginUserRequest(email, password))
        if (!response.isSuccessful) error("Сервер отклонил запрос: ${response.code()}")
        return response.body()?.userId ?: error("Неверная почта или пароль")
    }

    /** Создаёт аккаунт и возвращает userId; при неудаче выбрасывает исключение. */
    suspend fun register(name: String, email: String, password: String): String {
        val response = api.register(RegisterUserRequest(name, email, password))
        if (!response.isSuccessful) error("Сервер отклонил запрос: ${response.code()}")
        return response.body()?.userId ?: error("Аккаунт с такой почтой уже существует")
    }
}
