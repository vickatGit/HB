package com.example.habit.ui.activity.ChatsActivity

import com.example.habit.data.network.model.ChatModel.ChatModel
import com.example.habit.data.network.model.RoomModel.Room

sealed class ChatsUIState {
    data class ChatRooms(val chatRooms: List<Room>) : ChatsUIState()
    data class Chats(val chats: List<ChatModel>) : ChatsUIState()
    object Loading: ChatsUIState()
    data class Error(val error:String) : ChatsUIState()
    object Nothing: ChatsUIState()
}