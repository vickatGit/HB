package com.example.habit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.domain.UseCases.ChatUseCases.GetChatRoomsUseCase
import com.example.habit.domain.UseCases.ChatUseCases.GetChatsUseCase
import com.example.habit.ui.activity.ChatsActivity.ChatsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val getChatRoomsUseCase: GetChatRoomsUseCase,
    private val getChatsUseCase: GetChatsUseCase
) : ViewModel() {

    private var _chatsUIState = MutableStateFlow<ChatsUIState>(ChatsUIState.Nothing)
    val chatsUIState = _chatsUIState.asStateFlow()

    fun getChatRooms(){
        viewModelScope.launch {
            _chatsUIState.update { ChatsUIState.Loading }
            getChatRoomsUseCase().collect{rooms ->
                _chatsUIState.update { ChatsUIState.ChatRooms(rooms) }
            }
        }
    }

    fun getChats(roomId:String){
        viewModelScope.launch {
            _chatsUIState.update { ChatsUIState.Loading }
            getChatsUseCase(roomId).collectLatest {chats ->
                _chatsUIState.update { ChatsUIState.Chats(chats) }
            }
        }
    }
}