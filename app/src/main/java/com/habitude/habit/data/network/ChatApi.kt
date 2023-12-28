package com.habitude.habit.data.network

import com.habitude.habit.data.network.model.ChatModel.ChatModel
import com.habitude.habit.data.network.model.ChatModel.ChatsModel
import com.habitude.habit.data.network.model.RoomModel.RoomModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatApi {

    @GET("/chat/rooms/{userId}")
    suspend fun getRooms(@Path("userId") userId:String):Response<com.habitude.habit.data.network.model.RoomModel.RoomModel>
    
    @GET("chat/chats/{roomId}")
    suspend fun getChats(@Path("roomId") roomId:String):Response<ChatsModel>
}