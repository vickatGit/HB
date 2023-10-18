package com.example.habit.data.Repository

import com.example.habit.data.network.ChatApi
import com.example.habit.data.network.model.ChatModel.ChatModel
import com.example.habit.data.network.model.RoomModel.Room
import com.example.habit.domain.Repository.ChatRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRepoImpl(
    private val chatApi: ChatApi,
    private val userId: String) : ChatRepo {
    override suspend fun getRooms(): Flow<List<Room>> {
        val response = chatApi.getRooms(userId = userId)
        return flow {
            if(response.isSuccessful){
                response.body()?.data?.let {
                    emit(it)
                }
            }
        }

    }

    override suspend fun getChats(roomId:String): Flow<List<ChatModel>> {
        val response = chatApi.getChats(roomId)
        return flow {
            if(response.isSuccessful){
                response.body()?.let {
                    emit(it.chats)
                }
            }
        }
    }
}