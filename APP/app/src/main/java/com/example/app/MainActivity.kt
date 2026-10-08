package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.app.ui.UiState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

/**
 * Основной контейнер авторизованной части приложения.
 *
 * Управляет нижней навигацией, общим [SubscriptionsViewModel] и разрешением
 * на системные уведомления.
 */
class MainActivity : AppCompatActivity() {
    private val viewModel: SubscriptionsViewModel by viewModels()
    private lateinit var session: SessionManager
    private lateinit var bottomNavigation: BottomNavigationView
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    /** Создаёт оболочку приложения, восстанавливает экран и запускает загрузку данных. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        val userId = session.userId
        if (userId == null) {
            openWelcome()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.screen_shell)
        applyInsets()
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            showFragment(
                when (item.itemId) {
                    R.id.nav_subscriptions -> SubscriptionsFragment()
                    R.id.nav_profile -> ProfileFragment()
                    else -> HomeFragment()
                }
            )
            true
        }

        viewModel.state.observe(this) { state ->
            if (state is UiState.Error) {
                Snackbar.make(
                    findViewById(R.id.mainRoot),
                    humanizeNetworkError(state.message),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.nav_home
            viewModel.load(userId)
        } else if (viewModel.state.value is UiState.Idle) {
            viewModel.load(userId)
        }
        NotificationHelper.createChannel(this)
        NotificationScheduler.schedule(this, userId)
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            androidx.core.content.ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /** Переключает нижнее меню на экран всех подписок. */
    fun openSubscriptions() {
        bottomNavigation.selectedItemId = R.id.nav_subscriptions
    }

    /** Очищает сессию, отменяет напоминания и возвращает пользователя на стартовый экран. */
    fun logout() {
        NotificationScheduler.cancel(this)
        session.clear()
        openWelcome()
    }

    /** Показывает выбранный фрагмент внутри основного контейнера. */
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.contentContainer, fragment)
            .commit()
    }

    /** Открывает стартовый экран в новой очищенной цепочке Activity. */
    private fun openWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    /** Учитывает размеры системных панелей при edge-to-edge отображении. */
    private fun applyInsets() {
        val root = findViewById<View>(R.id.mainRoot)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    /** Заменяет сетевую ошибку Retrofit на подсказку о Docker API. */
    private fun humanizeNetworkError(message: String) = when {
        message.contains("connect", true) || message.contains("failed to", true) ->
            "Не удалось подключиться к API. Проверьте Docker на порту 8080."
        else -> message
    }
}
