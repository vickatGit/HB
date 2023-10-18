package com.example.habit.data.network.model.ChatModel

import com.google.gson.annotations.SerializedName

data class ChatsModel (
    @SerializedName("data")
    val chats:List<ChatModel>
)