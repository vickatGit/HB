package com.example.habit.domain.UseCases.ChatUseCases

import com.example.habit.data.network.model.RoomModel.Room
import com.example.habit.domain.Repository.ChatRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatRoomsUseCase @Inject constructor(
    private val chatRepo: ChatRepo
) {
    suspend operator fun invoke(): Flow<List<Room>> {
        return chatRepo.getRooms()
    }
}