package com.example.habit.data.network.model.ChatModel

data class MessageModel(
    val from: String,
    val to: String,
    val msg: String,
    val msgType: String,
    val mediaUrl: String
)
