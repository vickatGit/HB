package com.habitude.habit.ui.activity.LoginActivity

sealed class LoginUiState {
    data class Success(val message:String):LoginUiState()
    object Loading:LoginUiState()
    data class Error(val error:String) : LoginUiState()
    object Nothing:LoginUiState()
}
