package com.habitude.habit.domain.Repository

import com.habitude.habit.data.network.model.ChatModel.ChatModel
import com.habitude.habit.data.network.model.RoomModel.Room
import kotlinx.coroutines.flow.Flow

interface ChatRepo {
    suspend fun getRooms(): Flow<List<com.habitude.habit.data.network.model.RoomModel.Room>>

    suspend fun getChats(roomId:String): Flow<List<ChatModel>>
}