package com.example.habit.data.network

import com.example.habit.data.network.model.RoomModel.RoomModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatApi {

    @GET("/chat/rooms/{userId}")
    suspend fun getRooms(@Path("userId") userId:String):Response<RoomModel>
}