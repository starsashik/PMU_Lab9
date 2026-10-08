package com.example.app.api

/** JSON-тело регистрации: имя, почта и поле passwordHash, ожидаемое сервером. */
data class RegisterUserRequest(
    val name: String,
    val email: String,
    val passwordHash: String
)

/** JSON-тело входа с почтой и полем passwordHash, ожидаемым сервером. */
data class LoginUserRequest(
    val email: String,
    val passwordHash: String
)

/** Ответ авторизации; отсутствие userId означает неуспешную авторизацию. */
data class AuthResponse(val userId: String?)

/** Данные подписки, полученные от сервера, включая владельца и условия напоминаний. */
data class SubscriptionModel(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val paymentPeriod: String,
    val nextPaymentDate: String,
    val notificationDaysBefore: Int?,
    val isActive: Boolean?,
    val userId: String
)

/** Обёртка JSON-ответа со списком подписок. */
data class SubscriptionsResponse(val subscriptions: List<SubscriptionModel>)

/** Обёртка JSON-ответа с одной подпиской. */
data class GetSubscriptionResponse(val subscription: SubscriptionModel)

/** JSON-тело создания подписки с условиями оплаты и напоминаний. */
data class CreateSubscriptionRequest(
    val userId: String,
    val name: String,
    val description: String?,
    val price: Double,
    val paymentPeriod: String,
    val nextPaymentDate: String,
    val notificationDaysBefore: Int? = 3,
    val isActive: Boolean? = true
)

/** Ответ сервера с идентификатором созданной подписки. */
data class CreateSubscriptionResponse(val subscriptionId: String)

/** JSON-тело обновления подписки; идентификатор передаётся отдельно в URL. */
data class UpdateSubscriptionRequest(
    val userId: String,
    val name: String,
    val description: String?,
    val price: Double,
    val paymentPeriod: String,
    val nextPaymentDate: String,
    val notificationDaysBefore: Int? = 3,
    val isActive: Boolean? = true
)

/** Подтверждение обновления подписки с её идентификатором. */
data class UpdateSubscriptionResponse(val subscriptionId: String)

/** Подтверждение удаления подписки с её идентификатором. */
data class DeleteSubscriptionResponse(val subscriptionId: String)

/** Рассчитанные сервером показатели расходов, активности и ближайших платежей. */
data class StatisticsModel(
    val userId: String,
    val totalSubscriptions: Int,
    val activeSubscriptions: Int,
    val inactiveSubscriptions: Int,
    val monthlyExpenses: Double,
    val yearlyExpenses: Double,
    val paymentsNextSevenDays: Int,
    val paymentsAmountNextSevenDays: Double,
    val nearestPaymentDate: String?,
    val mostExpensiveSubscriptionName: String?,
    val mostExpensiveSubscriptionMonthlyCost: Double?
)

/** Обёртка JSON-ответа со статистикой пользователя. */
data class StatisticsResponse(val statistics: StatisticsModel)
