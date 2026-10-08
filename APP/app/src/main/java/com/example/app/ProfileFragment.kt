package com.example.app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.app.ui.UiState
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Показывает данные сессии, обновление данных, проверку уведомлений и выход. */
class ProfileFragment : Fragment(R.layout.screen_profile) {
    private val viewModel: SubscriptionsViewModel by activityViewModels()

    /** Заполняет профиль и назначает действия кнопкам обновления, уведомления и выхода. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        view.findViewById<TextView>(R.id.profileName).text = session.name
        view.findViewById<TextView>(R.id.profileEmail).text = session.email
        view.findViewById<TextView>(R.id.avatarLabel).text =
            session.name.firstOrNull()?.uppercase() ?: "S"
        view.findViewById<View>(R.id.refreshButton).setOnClickListener {
            session.userId?.let(viewModel::load)
        }
        view.findViewById<View>(R.id.testNotificationButton).setOnClickListener {
            val item = (viewModel.state.value as? UiState.Success)
                ?.data?.subscriptions
                ?.filter { it.isActive != false }
                ?.minByOrNull { it.nextPaymentDate }
            if (item == null) {
                Snackbar.make(view, "Сначала добавьте активную подписку", Snackbar.LENGTH_LONG).show()
            } else {
                val days = runCatching {
                    ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(item.nextPaymentDate))
                }.getOrDefault(0L).coerceAtLeast(0L)
                val shown = NotificationHelper.showUpcomingPayment(requireContext(), item, days)
                Snackbar.make(
                    view,
                    if (shown) "Уведомление отправлено" else "Разрешите уведомления в настройках Android",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        view.findViewById<View>(R.id.logoutButton).setOnClickListener {
            (activity as? MainActivity)?.logout()
        }
    }
}
