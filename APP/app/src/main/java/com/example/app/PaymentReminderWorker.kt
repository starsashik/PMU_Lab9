package com.example.app

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app.repository.SubscriptionsRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Фоновая задача, которая получает подписки сервера и проверяет приближение платежей. */
class PaymentReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    /**
     * Показывает напоминания по активным подпискам один раз в день.
     * При ошибке запроса просит WorkManager повторить выполнение позже.
     */
    override suspend fun doWork(): Result {
        val userId = inputData.getString(KEY_USER_ID) ?: return Result.failure()
        return try {
            val today = LocalDate.now()
            val shown = applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            SubscriptionsRepository().getForUser(userId)
                .filter { it.isActive != false }
                .forEach { subscription ->
                    val paymentDate = runCatching {
                        LocalDate.parse(subscription.nextPaymentDate)
                    }.getOrNull() ?: return@forEach
                    val daysLeft = ChronoUnit.DAYS.between(today, paymentDate)
                    val reminderDays = subscription.notificationDaysBefore ?: 3
                    val key = "${subscription.id}_${today}"
                    if (daysLeft in 0L..reminderDays.toLong() && !shown.getBoolean(key, false)) {
                        if (NotificationHelper.showUpcomingPayment(applicationContext, subscription, daysLeft)) {
                            shown.edit().putBoolean(key, true).apply()
                        }
                    }
                }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_USER_ID = "user_id"
        private const val PREFERENCES = "payment_notifications"
    }
}
