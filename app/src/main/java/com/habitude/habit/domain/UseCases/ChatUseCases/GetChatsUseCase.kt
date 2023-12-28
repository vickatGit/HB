package com.habitude.habit.domain.UseCases.ChatUseCases

import com.habitude.habit.data.network.model.ChatModel.ChatModel
import com.habitude.habit.domain.Repository.ChatRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val chatRepo: ChatRepo
) {
    suspend operator fun invoke(roomId:String): Flow<List<ChatModel>> {
        return chatRepo.getChats(roomId)
    }
}