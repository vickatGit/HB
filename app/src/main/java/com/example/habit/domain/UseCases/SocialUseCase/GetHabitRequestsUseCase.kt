package com.example.habit.domain.UseCases.SocialUseCase

import com.example.habit.data.network.model.HabitRequestModel.HabitRequestModel
import com.example.habit.domain.Repository.SocialRepo
import com.example.habit.domain.models.notification.HabitRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitRequestsUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(): Flow<List<HabitRequest>?> {
        return socialRepo.getHabitRequests()
    }
}