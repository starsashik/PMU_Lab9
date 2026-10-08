package com.example.app

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.app.ui.UiState
import java.text.NumberFormat
import java.util.Locale

/** Показывает расходы пользователя и первые три активные подписки. */
class HomeFragment : Fragment(R.layout.screen_home) {
    private val viewModel: SubscriptionsViewModel by activityViewModels()
    private val moneyFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU")).apply {
        maximumFractionDigits = 2
    }
    private val detailResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            SessionManager(requireContext()).userId?.let(viewModel::load)
        }
    }

    /** Подключает переходы к списку и наблюдение за общим состоянием подписок. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.userNameLabel).text = SessionManager(requireContext()).name
        view.findViewById<TextView>(R.id.seeAllButton).setOnClickListener {
            (activity as? MainActivity)?.openSubscriptions()
        }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) render(view, state.data)
        }
    }

    /** Заполняет статистику и список активных подписок данными сервера. */
    private fun render(view: View, data: DashboardData) {
        view.findViewById<TextView>(R.id.monthlyValue).text = money(data.statistics.monthlyExpenses)
        view.findViewById<TextView>(R.id.yearlyValue).text = money(data.statistics.yearlyExpenses)
        view.findViewById<TextView>(R.id.weekValue).text = money(data.statistics.paymentsAmountNextSevenDays)
        val items = data.subscriptions.filter { it.isActive != false }.take(3)
        view.findViewById<TextView>(R.id.homeEmptyLabel).visibility =
            if (items.isEmpty()) View.VISIBLE else View.GONE
        view.findViewById<RecyclerView>(R.id.homeSubscriptionsList).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = SubscriptionAdapter { subscription ->
                detailResult.launch(
                    Intent(requireContext(), SubscriptionDetailActivity::class.java)
                        .putExtra(SubscriptionDetailActivity.EXTRA_SUBSCRIPTION_ID, subscription.id)
                )
            }.also { it.submitList(items) }
        }
    }

    /** Форматирует сумму с русским разделителем разрядов и знаком рубля. */
    private fun money(value: Double) = "${moneyFormat.format(value)} ₽"
}
