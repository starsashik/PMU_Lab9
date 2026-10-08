package com.example.app.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Описывает HTTP-маршруты сервера; Retrofit создаёт реализацию интерфейса. */
interface ApiService {
    /** Регистрирует пользователя и возвращает HTTP-ответ с его идентификатором. */
    @POST("api/Authorization/RegisterUser")
    suspend fun register(@Body request: RegisterUserRequest): Response<AuthResponse>

    /** Проверяет данные входа и возвращает HTTP-ответ авторизации. */
    @POST("api/Authorization/LoginUser")
    suspend fun login(@Body request: LoginUserRequest): Response<AuthResponse>

    /** Получает общий список подписок; фильтрация по пользователю выполняется в репозитории. */
    @GET("api/Subscriptions")
    suspend fun getSubscriptions(): Response<SubscriptionsResponse>

    /** Получает сведения о подписке по идентификатору в URL. */
    @GET("api/Subscriptions/{id}")
    suspend fun getSubscription(
        @Path("id") id: String
    ): Response<GetSubscriptionResponse>

    /** Создаёт подписку из JSON-тела запроса. */
    @POST("api/Subscriptions")
    suspend fun createSubscription(
        @Body request: CreateSubscriptionRequest
    ): Response<CreateSubscriptionResponse>

    /** Обновляет поля подписки с указанным идентификатором. */
    @PUT("api/Subscriptions/{id}")
    suspend fun updateSubscription(
        @Path("id") id: String,
        @Body request: UpdateSubscriptionRequest
    ): Response<UpdateSubscriptionResponse>

    /** Удаляет подписку по идентификатору. */
    @DELETE("api/Subscriptions/{id}")
    suspend fun deleteSubscription(
        @Path("id") id: String
    ): Response<DeleteSubscriptionResponse>

    /** Получает рассчитанную сервером статистику пользователя. */
    @GET("api/Statistics/GetStatistics/{userId}")
    suspend fun getStatistics(
        @Path("userId") userId: String
    ): Response<StatisticsResponse>
}
