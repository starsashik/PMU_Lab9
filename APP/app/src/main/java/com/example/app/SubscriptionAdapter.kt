package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app.api.SubscriptionModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Связывает модели подписок с карточками RecyclerView и передаёт нажатия в onClick. */
class SubscriptionAdapter(
    private val onClick: (SubscriptionModel) -> Unit = {}
) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {
    private var items: List<SubscriptionModel> = emptyList()
    private val moneyFormat = NumberFormat.getNumberInstance(Locale("ru", "RU")).apply {
        maximumFractionDigits = 2
    }

    /** Заменяет отображаемый список и сообщает RecyclerView об обновлении. */
    fun submitList(newItems: List<SubscriptionModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    /** Создаёт карточку из item_subscription.xml и оборачивает её в ViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subscription, parent, false)
        return SubscriptionViewHolder(view)
    }

    /** Заполняет карточку данными подписки с указанной позицией. */
    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) = holder.bind(items[position])

    /** Возвращает количество карточек в списке. */
    override fun getItemCount() = items.size

    /** Хранит ссылки на элементы одной карточки подписки. */
    inner class SubscriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val initial: TextView = view.findViewById(R.id.serviceInitial)
        private val name: TextView = view.findViewById(R.id.serviceName)
        private val nextPayment: TextView = view.findViewById(R.id.nextPaymentLabel)
        private val price: TextView = view.findViewById(R.id.priceLabel)
        private val period: TextView = view.findViewById(R.id.periodLabel)

        /** Заполняет название, сумму, период, дату и обработчик нажатия на карточку. */
        fun bind(item: SubscriptionModel) {
            initial.text = item.name.trim().firstOrNull()?.uppercase() ?: "S"
            name.text = item.name
            price.text = "${moneyFormat.format(item.price)} ₽"
            period.text = when (item.paymentPeriod.lowercase()) {
                "weekly" -> "в неделю"
                "quarterly" -> "в квартал"
                "yearly" -> "в год"
                else -> "в месяц"
            }
            nextPayment.text = "Следующий платёж: ${formatDate(item.nextPaymentDate)}"
            itemView.alpha = if (item.isActive == false) 0.55f else 1f
            itemView.setOnClickListener { onClick(item) }
        }

        /** Преобразует дату сервера в краткую русскую дату; при ошибке возвращает исходную строку. */
        private fun formatDate(value: String): String = try {
            LocalDate.parse(value).format(DateTimeFormatter.ofPattern("d MMM", Locale("ru")))
        } catch (_: Exception) {
            value
        }
    }
}
