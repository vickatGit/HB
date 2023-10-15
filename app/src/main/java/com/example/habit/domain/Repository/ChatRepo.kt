package com.example.habit.domain.Repository

import com.example.habit.data.network.model.RoomModel.Room
import kotlinx.coroutines.flow.Flow

interface ChatRepo {
    suspend fun getRooms(): Flow<List<Room>>
}