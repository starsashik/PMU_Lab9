package com.example.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.app.api.SubscriptionModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Создаёт канал и системные уведомления с переходом к карточке подписки. */
object NotificationHelper {
    const val CHANNEL_ID = "upcoming_payments"

    /** Регистрирует канал платежей; повторный вызов безопасен и не создаёт дубликат. */
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Предстоящие платежи",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Напоминания о скором списании денег за подписки"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    /**
     * Публикует напоминание о платеже.
     *
     * @param daysLeft количество дней до оплаты.
     * @return true, если уведомление отправлено; false, если разрешение не предоставлено.
     */
    fun showUpcomingPayment(context: Context, item: SubscriptionModel, daysLeft: Long): Boolean {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return false

        createChannel(context)
        val intent = Intent(context, SubscriptionDetailActivity::class.java).apply {
            putExtra(SubscriptionDetailActivity.EXTRA_SUBSCRIPTION_ID, item.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            item.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val date = runCatching {
            LocalDate.parse(item.nextPaymentDate)
                .format(DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru-RU")))
        }.getOrDefault(item.nextPaymentDate)
        val timing = when (daysLeft) {
            0L -> "сегодня"
            1L -> "завтра"
            else -> "через $daysLeft дн."
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_wallet)
            .setContentTitle("Скоро платёж: ${item.name}")
            .setContentText("${item.price} ₽ — $timing, $date")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "${item.price} ₽ будут списаны $timing ($date). Нажмите, чтобы открыть подписку."
            ))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(item.id.hashCode(), notification)
        return true
    }
}
