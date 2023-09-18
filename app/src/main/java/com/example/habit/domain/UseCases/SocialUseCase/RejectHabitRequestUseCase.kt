package com.example.habit.domain.UseCases.SocialUseCase

import com.example.habit.domain.Repository.SocialRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RejectHabitRequestUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(habitGroupId:String): Flow<Any> {
        return socialRepo.rejectHabitRequest(habitGroupId)
    }
}