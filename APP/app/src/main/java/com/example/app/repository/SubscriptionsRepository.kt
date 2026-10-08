package com.example.app.repository

import com.example.app.api.CreateSubscriptionRequest
import com.example.app.api.RetrofitClient
import com.example.app.api.StatisticsModel
import com.example.app.api.SubscriptionModel
import com.example.app.api.UpdateSubscriptionRequest

/** Предоставляет операции с подписками и статистикой, проверяя HTTP-ответы API. */
class SubscriptionsRepository {
    private val api = RetrofitClient.api

    /** Получает общий список сервера и оставляет подписки указанного пользователя. */
    suspend fun getForUser(userId: String): List<SubscriptionModel> {
        val response = api.getSubscriptions()
        if (!response.isSuccessful) error("Не удалось загрузить подписки: ${response.code()}")
        return response.body()?.subscriptions
            ?.filter { it.userId.equals(userId, ignoreCase = true) }
            .orEmpty()
    }

    /** Возвращает статистику пользователя; при ошибке или пустом ответе выбрасывает исключение. */
    suspend fun getStatistics(userId: String): StatisticsModel {
        val response = api.getStatistics(userId)
        if (!response.isSuccessful) error("Не удалось загрузить статистику: ${response.code()}")
        return response.body()?.statistics ?: error("Сервер вернул пустую статистику")
    }

    /** Отправляет запрос создания подписки и проверяет наличие её идентификатора в ответе. */
    suspend fun create(request: CreateSubscriptionRequest) {
        val response = api.createSubscription(request)
        if (!response.isSuccessful || response.body()?.subscriptionId == null) {
            error("Не удалось добавить подписку: ${response.code()}")
        }
    }

    /** Загружает одну подписку по идентификатору. */
    suspend fun get(id: String): SubscriptionModel {
        val response = api.getSubscription(id)
        if (!response.isSuccessful) error("Не удалось загрузить подписку: ${response.code()}")
        return response.body()?.subscription ?: error("Сервер вернул пустой ответ")
    }

    /** Обновляет подписку через PUT и проверяет подтверждение сервера. */
    suspend fun update(id: String, request: UpdateSubscriptionRequest) {
        val response = api.updateSubscription(id, request)
        if (!response.isSuccessful || response.body()?.subscriptionId == null) {
            error("Не удалось сохранить подписку: ${response.code()}")
        }
    }

    /** Удаляет подписку через DELETE и проверяет подтверждение сервера. */
    suspend fun delete(id: String) {
        val response = api.deleteSubscription(id)
        if (!response.isSuccessful || response.body()?.subscriptionId == null) {
            error("Не удалось удалить подписку: ${response.code()}")
        }
    }
}
