package com.example.habit.ui.activity.ChatsActivity

import com.example.habit.data.network.model.RoomModel.Room
import com.example.habit.ui.activity.LoginActivity.LoginUiState

sealed class ChatsUIState {
    data class ChatRooms(val chatRooms: List<Room>) : ChatsUIState()
    object Loading: ChatsUIState()
    data class Error(val error:String) : ChatsUIState()
    object Nothing: ChatsUIState()
}