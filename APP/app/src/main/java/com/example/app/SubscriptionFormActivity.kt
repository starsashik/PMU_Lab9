package com.example.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.app.api.CreateSubscriptionRequest
import com.example.app.api.SubscriptionModel
import com.example.app.api.UpdateSubscriptionRequest
import com.example.app.repository.SubscriptionsRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Экран создания и редактирования подписки; режим определяется идентификатором в Intent. */
class SubscriptionFormActivity : AppCompatActivity() {
    private val repository = SubscriptionsRepository()
    private val periods = linkedMapOf(
        "Еженедельно" to "weekly",
        "Ежемесячно" to "monthly",
        "Ежеквартально" to "quarterly",
        "Ежегодно" to "yearly"
    )
    private val subscriptionId: String? by lazy { intent.getStringExtra(EXTRA_SUBSCRIPTION_ID) }

    /** Настраивает поля, календарь и кнопку сохранения; при первом открытии загружает исходные значения. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_subscription_form)
        applyInsets()

        findViewById<View>(R.id.formBackButton).setOnClickListener { finish() }
        findViewById<TextView>(R.id.formTitle).setText(
            if (subscriptionId == null) R.string.add_subscription else R.string.edit_subscription
        )
        val periodInput = findViewById<AutoCompleteTextView>(R.id.periodInput)
        periodInput.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, periods.keys.toList())
        )
        findViewById<EditText>(R.id.dateInput).setOnClickListener { showDatePicker() }
        findViewById<MaterialButton>(R.id.saveSubscriptionButton).setOnClickListener { save() }

        if (savedInstanceState == null) {
            if (subscriptionId == null) {
                periodInput.setText("Ежемесячно", false)
                findViewById<EditText>(R.id.dateInput)
                    .setText(LocalDate.now().plusMonths(1).toString())
                findViewById<EditText>(R.id.reminderDaysInput).setText("3")
            } else {
                loadSubscription(subscriptionId!!)
            }
        }
    }

    /** Загружает существующую подписку для редактирования и заполняет форму. */
    private fun loadSubscription(id: String) {
        setLoading(true)
        lifecycleScope.launch {
            runCatching { repository.get(id) }
                .onSuccess(::fillForm)
                .onFailure { showError(it.localizedMessage ?: "Не удалось загрузить подписку") }
            setLoading(false)
        }
    }

    /** Переносит данные подписки в поля формы и переключатель активности. */
    private fun fillForm(item: SubscriptionModel) {
        findViewById<EditText>(R.id.subscriptionNameInput).setText(item.name)
        findViewById<EditText>(R.id.priceInput).setText(item.price.toString())
        findViewById<AutoCompleteTextView>(R.id.periodInput)
            .setText(periods.entries.firstOrNull { it.value == item.paymentPeriod }?.key ?: "Ежемесячно", false)
        findViewById<EditText>(R.id.dateInput).setText(item.nextPaymentDate)
        findViewById<EditText>(R.id.reminderDaysInput)
            .setText((item.notificationDaysBefore ?: 3).toString())
        findViewById<EditText>(R.id.descriptionInput).setText(item.description.orEmpty())
        findViewById<MaterialSwitch>(R.id.activeSwitch).isChecked = item.isActive != false
    }

    /** Проверяет поля, выполняет POST или PUT и возвращает RESULT_OK при успешном сохранении. */
    private fun save() {
        val userId = SessionManager(this).userId ?: return finish()
        val name = findViewById<EditText>(R.id.subscriptionNameInput).text.toString().trim()
        val price = findViewById<EditText>(R.id.priceInput).text.toString()
            .replace(',', '.').toDoubleOrNull()
        val date = findViewById<EditText>(R.id.dateInput).text.toString()
        val reminderDays = findViewById<EditText>(R.id.reminderDaysInput).text.toString().toIntOrNull()
        val nameLayout = findViewById<TextInputLayout>(R.id.subscriptionNameLayout)
        val priceLayout = findViewById<TextInputLayout>(R.id.priceLayout)
        val dateLayout = findViewById<TextInputLayout>(R.id.dateLayout)
        val reminderLayout = findViewById<TextInputLayout>(R.id.reminderDaysLayout)
        nameLayout.error = if (name.isBlank()) "Введите название" else null
        priceLayout.error = if (price == null || price <= 0) "Введите стоимость больше нуля" else null
        dateLayout.error = if (runCatching { LocalDate.parse(date) }.isFailure) "Выберите дату" else null
        reminderLayout.error = if (reminderDays == null || reminderDays !in 0..365) "Введите число от 0 до 365" else null
        if (listOf(nameLayout, priceLayout, dateLayout, reminderLayout).any { it.error != null }) return

        val description = findViewById<EditText>(R.id.descriptionInput)
            .text.toString().trim().ifBlank { null }
        val period = periods[findViewById<AutoCompleteTextView>(R.id.periodInput).text.toString()]
            ?: "monthly"
        val active = findViewById<MaterialSwitch>(R.id.activeSwitch).isChecked
        setLoading(true)
        lifecycleScope.launch {
            runCatching {
                val id = subscriptionId
                if (id == null) {
                    repository.create(
                        CreateSubscriptionRequest(
                            userId, name, description, price!!, period, date, reminderDays, active
                        )
                    )
                } else {
                    repository.update(
                        id,
                        UpdateSubscriptionRequest(
                            userId, name, description, price!!, period, date, reminderDays, active
                        )
                    )
                }
            }.onSuccess {
                NotificationScheduler.schedule(this@SubscriptionFormActivity, userId)
                setResult(RESULT_OK)
                finish()
            }.onFailure {
                setLoading(false)
                showError(it.localizedMessage ?: "Не удалось сохранить подписку")
            }
        }
    }

    /** Открывает календарь и записывает выбранную дату в формате ISO: ГГГГ-ММ-ДД. */
    private fun showDatePicker() {
        val current = runCatching {
            LocalDate.parse(findViewById<EditText>(R.id.dateInput).text.toString())
        }.getOrDefault(LocalDate.now())
        DatePickerDialog(
            this,
            { _, year, month, day ->
                findViewById<EditText>(R.id.dateInput)
                    .setText(LocalDate.of(year, month + 1, day).toString())
            },
            current.year,
            current.monthValue - 1,
            current.dayOfMonth
        ).show()
    }

    /** Показывает индикатор запроса и блокирует повторное нажатие кнопки сохранения. */
    private fun setLoading(loading: Boolean) {
        findViewById<ProgressBar>(R.id.formProgress).visibility = if (loading) View.VISIBLE else View.GONE
        findViewById<MaterialButton>(R.id.saveSubscriptionButton).isEnabled = !loading
    }

    /** Показывает сообщение об ошибке в Snackbar поверх формы. */
    private fun showError(message: String) {
        Snackbar.make(findViewById(R.id.formRoot), message, Snackbar.LENGTH_LONG).show()
    }

    /** Добавляет отступы системных панелей для edge-to-edge разметки. */
    private fun applyInsets() {
        val root = findViewById<View>(R.id.formRoot)
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
