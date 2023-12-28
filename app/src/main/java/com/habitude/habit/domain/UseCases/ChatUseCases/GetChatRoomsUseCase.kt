package com.habitude.habit.domain.UseCases.ChatUseCases

import com.habitude.habit.data.network.model.RoomModel.Room
import com.habitude.habit.domain.Repository.ChatRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatRoomsUseCase @Inject constructor(
    private val chatRepo: ChatRepo
) {
    suspend operator fun invoke(): Flow<List<com.habitude.habit.data.network.model.RoomModel.Room>> {
        return chatRepo.getRooms()
    }
}