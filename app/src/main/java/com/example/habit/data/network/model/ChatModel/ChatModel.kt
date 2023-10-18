package com.example.habit.data.network.model.ChatModel

import com.google.gson.annotations.SerializedName

data class ChatModel(
    @SerializedName("_id")
    val id: String?,
    val from: String,
    val toRoom: String?,
    val msg: String,
    val msgType: String,
    val mediaUrl: String
)
