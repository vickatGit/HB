package com.example.habit.domain.UseCases.ChatUseCases

import com.example.habit.data.network.model.ChatModel.ChatModel
import com.example.habit.domain.Repository.ChatRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val chatRepo: ChatRepo
) {
    suspend operator fun invoke(roomId:String): Flow<List<ChatModel>> {
        return chatRepo.getChats(roomId)
    }
}