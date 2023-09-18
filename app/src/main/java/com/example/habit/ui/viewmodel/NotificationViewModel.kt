package com.example.habit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.SocialUseCase.AcceptHabitRequestUseCase
import com.example.habit.domain.UseCases.SocialUseCase.GetHabitRequestsUseCase
import com.example.habit.domain.UseCases.SocialUseCase.RejectHabitRequestUseCase
import com.example.habit.ui.activity.NotificationActivity.NotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getHabitRequestsUseCase: GetHabitRequestsUseCase,
    private val acceptHabitRequestUseCase: AcceptHabitRequestUseCase,
    private val rejectHabitRequestUseCase: RejectHabitRequestUseCase
): ViewModel() {
    private var _uiState =  MutableStateFlow<NotificationUiState>(NotificationUiState.Nothing)
    val uiState = _uiState.asStateFlow()

    fun getNotifications(){
        viewModelScope.launch {
        _uiState.update { NotificationUiState.Loading }
            try {
                getHabitRequestsUseCase().catch {  }.collect{ habitRequests ->
                    _uiState.update { NotificationUiState.Success(habitRequests) }
                }
            }catch (e:Exception){
                _uiState.update { NotificationUiState.Error("${e.message}") }
            }
        }
    }

    fun acceptHabitRequest(habitGroupId:String){
        viewModelScope.launch {
            _uiState.update { NotificationUiState.Loading }
            try {
                acceptHabitRequestUseCase(habitGroupId).catch {  }.collect{ habitRequests ->
                    _uiState.update { NotificationUiState.RequestAccepted("You've Successfully joined the group") }
                }
            }catch (e:Exception){
                _uiState.update { NotificationUiState.Error("${e.message}") }
            }
        }
    }

    fun rejectHabitRequest(habitGroupId:String){
        viewModelScope.launch {
            _uiState.update { NotificationUiState.Loading }
            try {
                rejectHabitRequestUseCase(habitGroupId).catch {  }.collect{ habitRequests ->
                    _uiState.update { NotificationUiState.RequestRejected }
                }
            }catch (e:Exception){
                _uiState.update { NotificationUiState.Error("${e.message}") }
            }
        }
    }

}