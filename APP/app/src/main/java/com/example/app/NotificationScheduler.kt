package com.example.app

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/** Планирует фоновые проверки платежей через WorkManager. */
object NotificationScheduler {
    private const val PERIODIC_WORK = "subtrack_payment_reminders"

    /** Создаёт ежедневную и немедленную проверки платежей для указанного пользователя. */
    fun schedule(context: Context, userId: String) {
        val input = Data.Builder()
            .putString(PaymentReminderWorker.KEY_USER_ID, userId)
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val daily = PeriodicWorkRequestBuilder<PaymentReminderWorker>(24, TimeUnit.HOURS)
            .setInputData(input)
            .setConstraints(constraints)
            .addTag(PERIODIC_WORK)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            daily
        )
        WorkManager.getInstance(context).enqueue(
            OneTimeWorkRequestBuilder<PaymentReminderWorker>()
                .setInputData(input)
                .setConstraints(constraints)
                .addTag(PERIODIC_WORK)
                .build()
        )
    }

    /** Отменяет ежедневную проверку и все задачи напоминаний с общим тегом. */
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK)
        WorkManager.getInstance(context).cancelAllWorkByTag(PERIODIC_WORK)
    }
}
