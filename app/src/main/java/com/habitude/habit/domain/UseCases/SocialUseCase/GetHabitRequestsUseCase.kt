package com.habitude.habit.domain.UseCases.SocialUseCase

import com.habitude.habit.data.network.model.HabitRequestModel.HabitRequestModel
import com.habitude.habit.domain.Repository.SocialRepo
import com.habitude.habit.domain.models.notification.HabitRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitRequestsUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(): Flow<List<HabitRequest>?> {
        return socialRepo.getHabitRequests()
    }
}