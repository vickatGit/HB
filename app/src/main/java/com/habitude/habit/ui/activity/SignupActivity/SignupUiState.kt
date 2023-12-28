package com.habitude.habit.ui.activity.SignupActivity

import com.habitude.habit.ui.activity.LoginActivity.LoginUiState

sealed class SignupUiState {
    data class Success(val message:String): SignupUiState()
    object Loading: SignupUiState()
    data class Error(val error:String) : SignupUiState()
    object Nothing: SignupUiState()
}

