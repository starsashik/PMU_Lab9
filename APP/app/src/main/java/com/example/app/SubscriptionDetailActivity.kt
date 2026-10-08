package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.app.api.SubscriptionModel
import com.example.app.repository.SubscriptionsRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Показывает сведения одной подписки и предоставляет действия редактирования и удаления. */
class SubscriptionDetailActivity : AppCompatActivity() {
    private val repository = SubscriptionsRepository()
    private val subscriptionId: String by lazy {
        intent.getStringExtra(EXTRA_SUBSCRIPTION_ID).orEmpty()
    }
    private var subscription: SubscriptionModel? = null
    private val editor = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            setResult(RESULT_OK)
            loadSubscription()
        }
    }

    /** Читает идентификатор из Intent, подключает кнопки и загружает подписку. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (subscriptionId.isBlank()) return finish()
        enableEdgeToEdge()
        setContentView(R.layout.activity_subscription_detail)
        applyInsets()
        findViewById<View>(R.id.detailBackButton).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.editSubscriptionButton).setOnClickListener {
            editor.launch(
                Intent(this, SubscriptionFormActivity::class.java)
                    .putExtra(SubscriptionFormActivity.EXTRA_SUBSCRIPTION_ID, subscriptionId)
            )
        }
        findViewById<MaterialButton>(R.id.deleteSubscriptionButton).setOnClickListener {
            confirmDelete()
        }
        loadSubscription()
    }

    /** Получает актуальную подписку с сервера и отображает её данные. */
    private fun loadSubscription() {
        setLoading(true)
        lifecycleScope.launch {
            runCatching { repository.get(subscriptionId) }
                .onSuccess {
                    subscription = it
                    render(it)
                }
                .onFailure { showError(it.localizedMessage ?: "Не удалось загрузить подписку") }
            setLoading(false)
        }
    }

    /** Заполняет название, статус, цену, дату, описание и срок напоминания. */
    private fun render(item: SubscriptionModel) {
        val money = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU")).apply {
            maximumFractionDigits = 2
        }
        findViewById<TextView>(R.id.detailInitial).text = item.name.firstOrNull()?.uppercase() ?: "S"
        findViewById<TextView>(R.id.detailName).text = item.name
        findViewById<TextView>(R.id.detailStatus).text = if (item.isActive == false) "Приостановлена" else "Активна"
        val period = when (item.paymentPeriod.lowercase()) {
            "weekly" -> "в неделю"
            "quarterly" -> "в квартал"
            "yearly" -> "в год"
            else -> "в месяц"
        }
        findViewById<TextView>(R.id.detailPrice).text = "${money.format(item.price)} ₽ $period"
        findViewById<TextView>(R.id.detailDate).text = runCatching {
            LocalDate.parse(item.nextPaymentDate)
                .format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("ru-RU")))
        }.getOrDefault(item.nextPaymentDate)
        findViewById<TextView>(R.id.detailReminder).text =
            "Напоминание за ${item.notificationDaysBefore ?: 3} дн."
        findViewById<TextView>(R.id.detailDescription).text =
            item.description?.takeIf(String::isNotBlank) ?: "Описание не добавлено"
    }

    /** Показывает диалог подтверждения перед удалением подписки. */
    private fun confirmDelete() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Удалить подписку?")
            .setMessage("${subscription?.name.orEmpty()} исчезнет из списка. Это действие нельзя отменить.")
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ -> deleteSubscription() }
            .show()
    }

    /** Удаляет подписку через API и закрывает экран с RESULT_OK. */
    private fun deleteSubscription() {
        setLoading(true)
        lifecycleScope.launch {
            runCatching { repository.delete(subscriptionId) }
                .onSuccess {
                    setResult(RESULT_OK)
                    finish()
                }
                .onFailure {
                    setLoading(false)
                    showError(it.localizedMessage ?: "Не удалось удалить подписку")
                }
        }
    }

    /** Показывает индикатор загрузки и блокирует действия на время запроса. */
    private fun setLoading(loading: Boolean) {
        findViewById<ProgressBar>(R.id.detailProgress).visibility = if (loading) View.VISIBLE else View.GONE
        findViewById<MaterialButton>(R.id.editSubscriptionButton).isEnabled = !loading
        findViewById<MaterialButton>(R.id.deleteSubscriptionButton).isEnabled = !loading
    }

    /** Отображает ошибку загрузки или удаления в Snackbar. */
    private fun showError(message: String) {
        Snackbar.make(findViewById(R.id.detailRoot), message, Snackbar.LENGTH_LONG).show()
    }

    /** Учитывает системные панели, чтобы содержимое экрана оставалось доступным. */
    private fun applyInsets() {
        val root = findViewById<View>(R.id.detailRoot)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    companion object {
        const val EXTRA_SUBSCRIPTION_ID = "subscription_id"
    }
}
