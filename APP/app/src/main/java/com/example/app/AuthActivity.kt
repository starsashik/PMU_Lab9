package com.example.app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app.ui.UiState
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

/** Экран регистрации и входа с общей формой и переключением режимов. */
class AuthActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var session: SessionManager
    private lateinit var submitButton: MaterialButton
    private lateinit var progress: ProgressBar
    private lateinit var errorLabel: TextView
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private var isRegistration = true

    /** Инициализирует форму, выбирает режим из Intent и подключает наблюдение за состоянием. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.screen_auth)
        session = SessionManager(this)
        applyInsets()
        bindViews()
        observeState()

        isRegistration = intent.getBooleanExtra(EXTRA_REGISTRATION, true)
        findViewById<View>(R.id.backButton).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.loginTab).setOnClickListener { updateMode(false) }
        findViewById<MaterialButton>(R.id.registerTab).setOnClickListener { updateMode(true) }
        submitButton.setOnClickListener { submit() }
        updateMode(isRegistration)
    }

    /** Сохраняет ссылки на часто используемые элементы разметки. */
    private fun bindViews() {
        submitButton = findViewById(R.id.authSubmitButton)
        progress = findViewById(R.id.authProgress)
        errorLabel = findViewById(R.id.authError)
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
    }

    /** Обновляет интерфейс при загрузке, ошибке или успешной авторизации. */
    private fun observeState() {
        viewModel.state.observe(this) { state ->
            progress.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
            submitButton.isEnabled = state !is UiState.Loading
            when (state) {
                is UiState.Error -> {
                    errorLabel.text = humanizeNetworkError(state.message)
                    errorLabel.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    val email = emailInput.text.toString().trim()
                    session.save(state.data, nameInput.text.toString().trim(), email)
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
                else -> errorLabel.visibility = View.GONE
            }
        }
    }

    /**
     * Переключает форму между регистрацией и входом.
     *
     * @param registration нужно ли показывать поля и подписи регистрации.
     */
    private fun updateMode(registration: Boolean) {
        isRegistration = registration
        viewModel.reset()
        val loginTab = findViewById<MaterialButton>(R.id.loginTab)
        val registerTab = findViewById<MaterialButton>(R.id.registerTab)
        findViewById<TextInputLayout>(R.id.nameLayout).visibility = if (registration) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.authTitle)
            .setText(if (registration) R.string.register else R.string.login)
        findViewById<TextView>(R.id.authSubtitle).text =
            if (registration) "Создайте аккаунт за минуту" else "С возвращением! Введите свои данные"
        submitButton.setText(if (registration) R.string.register else R.string.login)
        registerTab.setBackgroundColor(getColor(if (registration) R.color.green else android.R.color.transparent))
        registerTab.setTextColor(getColor(if (registration) R.color.white else R.color.green))
        loginTab.setBackgroundColor(getColor(if (registration) android.R.color.transparent else R.color.green))
        loginTab.setTextColor(getColor(if (registration) R.color.green else R.color.white))
        errorLabel.visibility = View.GONE
    }

    /** Проверяет введённые данные и отправляет запрос через [AuthViewModel]. */
    private fun submit() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val nameLayout = findViewById<TextInputLayout>(R.id.nameLayout)
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        nameLayout.error = if (isRegistration && name.length < 2) "Введите имя" else null
        emailLayout.error = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Проверьте адрес почты" else null
        passwordLayout.error = if (password.length < 4) "Минимум 4 символа" else null
        if (nameLayout.error != null || emailLayout.error != null || passwordLayout.error != null) return
        if (isRegistration) viewModel.register(name, email, password) else viewModel.login(email, password)
    }

    /** Добавляет системные отступы, чтобы контент не попадал под статус-бар и навигацию. */
    private fun applyInsets() {
        val root = findViewById<View>(R.id.authRoot)
        val initialLeft = root.paddingLeft
        val initialTop = root.paddingTop
        val initialRight = root.paddingRight
        val initialBottom = root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(initialLeft + bars.left, initialTop + bars.top,
                initialRight + bars.right, initialBottom + bars.bottom)
            insets
        }
    }

    /** Преобразует техническую сетевую ошибку в понятное пользователю сообщение. */
    private fun humanizeNetworkError(message: String) = when {
        message.contains("connect", true) || message.contains("failed to", true) ->
            "Не удалось подключиться к API. Проверьте Docker на порту 8080."
        else -> message
    }

    companion object {
        const val EXTRA_REGISTRATION = "registration"
    }
}
