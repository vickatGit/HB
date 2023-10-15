package com.example.habit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.ChatUseCases.GetChatRoomsUseCase
import com.example.habit.ui.activity.ChatsActivity.ChatsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val getChatRoomsUseCase: GetChatRoomsUseCase
) : ViewModel() {

    private var _chatsUIState = MutableStateFlow<ChatsUIState>(ChatsUIState.Nothing)
    val chatsUIState = _chatsUIState.asStateFlow()

    fun getChatRooms(){
        viewModelScope.launch {
            getChatRoomsUseCase().collect{rooms ->
                _chatsUIState.update { ChatsUIState.ChatRooms(rooms) }
            }
        }
    }
}