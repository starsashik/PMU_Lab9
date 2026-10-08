package com.example.app

import android.os.Bundle
import android.view.View
import android.content.Intent
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.ui.UiState
import androidx.activity.result.contract.ActivityResultContracts

/** Отображает все подписки пользователя и запускает экраны создания и просмотра. */
class SubscriptionsFragment : Fragment(R.layout.screen_subscriptions) {
    private val viewModel: SubscriptionsViewModel by activityViewModels()
    private lateinit var adapter: SubscriptionAdapter
    private val formResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) reload()
    }

    /** Настраивает RecyclerView, переходы к подпискам и наблюдение за загрузкой. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SubscriptionAdapter { subscription ->
            formResult.launch(
                Intent(requireContext(), SubscriptionDetailActivity::class.java)
                    .putExtra(SubscriptionDetailActivity.EXTRA_SUBSCRIPTION_ID, subscription.id)
            )
        }
        view.findViewById<RecyclerView>(R.id.subscriptionsList).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SubscriptionsFragment.adapter
        }
        view.findViewById<View>(R.id.addSubscriptionButton).setOnClickListener {
            formResult.launch(Intent(requireContext(), SubscriptionFormActivity::class.java))
        }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            view.findViewById<ProgressBar>(R.id.subscriptionsProgress).visibility =
                if (state is UiState.Loading) View.VISIBLE else View.GONE
            if (state is UiState.Success) render(view, state.data)
        }
    }

    /** Обновляет список, количество подписок и видимость пустого состояния. */
    private fun render(view: View, data: DashboardData) {
        val items = data.subscriptions
        adapter.submitList(items)
        view.findViewById<TextView>(R.id.subscriptionsCount).text =
            "${items.size} ${subscriptionWord(items.size)}"
        view.findViewById<View>(R.id.emptyState).visibility =
            if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    /** Повторно загружает данные текущего пользователя после изменения подписок. */
    private fun reload() {
        SessionManager(requireContext()).userId?.let(viewModel::load)
    }

    /** Выбирает правильную форму слова «подписка» для указанного количества. */
    private fun subscriptionWord(count: Int) = when {
        count % 10 == 1 && count % 100 != 11 -> "подписка"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "подписки"
        else -> "подписок"
    }
}
