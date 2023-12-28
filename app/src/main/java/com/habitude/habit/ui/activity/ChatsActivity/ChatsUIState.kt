package com.habitude.habit.ui.activity.ChatsActivity

import com.habitude.habit.data.network.model.ChatModel.ChatModel
import com.habitude.habit.data.network.model.RoomModel.Room

sealed class ChatsUIState {
    data class ChatRooms(val chatRooms: List<com.habitude.habit.data.network.model.RoomModel.Room>) : ChatsUIState()
    data class Chats(val chats: List<ChatModel>) : ChatsUIState()
    object Loading: ChatsUIState()
    data class Error(val error:String) : ChatsUIState()
    object Nothing: ChatsUIState()
}