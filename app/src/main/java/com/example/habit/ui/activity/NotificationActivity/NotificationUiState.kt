package com.example.habit.ui.activity.NotificationActivity

import com.example.habit.domain.models.notification.HabitRequest

sealed class NotificationUiState {
    data class Success(val notifications: List<HabitRequest>?): NotificationUiState()
    object Loading: NotificationUiState()
    data class Error(val error:String) : NotificationUiState()
    object Nothing: NotificationUiState()
}