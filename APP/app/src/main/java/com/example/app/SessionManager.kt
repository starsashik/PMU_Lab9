package com.example.app

import android.content.Context

/** Хранит идентификатор, имя и почту вошедшего пользователя в SharedPreferences. */
class SessionManager(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    val userId: String?
        get() = preferences.getString(KEY_USER_ID, null)

    val name: String
        get() = preferences.getString(KEY_NAME, "SubTrack").orEmpty().ifBlank { "SubTrack" }

    val email: String
        get() = preferences.getString(KEY_EMAIL, "").orEmpty()

    /** Сохраняет данные авторизованного пользователя между запусками приложения. */
    fun save(userId: String, name: String, email: String) {
        preferences.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_NAME, name.ifBlank { email.substringBefore('@') })
            .putString(KEY_EMAIL, email)
            .apply()
    }

    /** Удаляет сохранённые данные сессии при выходе из аккаунта. */
    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "subtrack_session"
        const val KEY_USER_ID = "user_id"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
    }
}
