package com.example.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

/**
 * Стартовый экран приложения.
 *
 * Перенаправляет авторизованного пользователя на главный экран, а новому
 * пользователю предлагает регистрацию или вход.
 */
class WelcomeActivity : AppCompatActivity() {
    /** Создаёт стартовый экран и назначает обработчики кнопок входа и регистрации. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SessionManager(this).userId != null) {
            openMain()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.screen_welcome)
        val root = findViewById<android.view.View>(R.id.welcomeRoot)
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

        findViewById<MaterialButton>(R.id.startButton).setOnClickListener {
            openAuth(registration = true)
        }
        findViewById<MaterialButton>(R.id.existingAccountButton).setOnClickListener {
            openAuth(registration = false)
        }
    }

    /**
     * Открывает экран авторизации в требуемом режиме.
     *
     * @param registration `true` для регистрации, `false` для входа.
     */
    private fun openAuth(registration: Boolean) {
        startActivity(Intent(this, AuthActivity::class.java).apply {
            putExtra(AuthActivity.EXTRA_REGISTRATION, registration)
        })
    }

    /** Открывает главный экран и закрывает стартовый, чтобы нельзя было вернуться назад. */
    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
