package com.example.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.api.CreateSubscriptionRequest
import com.example.app.api.StatisticsModel
import com.example.app.api.SubscriptionModel
import com.example.app.repository.SubscriptionsRepository
import com.example.app.ui.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/** Объединяет список подписок и статистику для отображения на главной и в списке. */
data class DashboardData(
    val subscriptions: List<SubscriptionModel>,
    val statistics: StatisticsModel
)

/** Общая модель состояния главной и списка подписок; сохраняется при повороте Activity. */
class SubscriptionsViewModel : ViewModel() {
    private val repository = SubscriptionsRepository()
    private val _state = MutableLiveData<UiState<DashboardData>>(UiState.Idle)
    val state: LiveData<UiState<DashboardData>> = _state

    /** Параллельно загружает подписки пользователя и статистику, затем публикует результат. */
    fun load(userId: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                val subscriptions = async { repository.getForUser(userId) }
                val statistics = async { repository.getStatistics(userId) }
                UiState.Success(DashboardData(subscriptions.await(), statistics.await()))
            } catch (error: Exception) {
                UiState.Error(error.localizedMessage ?: "Не удалось обновить данные")
            }
        }
    }

    /** Создаёт подписку и повторно загружает данные; при сбое публикует ошибку. */
    fun create(request: CreateSubscriptionRequest) {
        viewModelScope.launch {
            try {
                repository.create(request)
                load(request.userId)
            } catch (error: Exception) {
                _state.value = UiState.Error(error.localizedMessage ?: "Не удалось добавить подписку")
            }
        }
    }
}
