package com.example.habit.domain.UseCases.SocialUseCase

import com.example.habit.domain.Repository.SocialRepo
import com.example.habit.domain.models.Follow.Follow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMembersUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(): Flow<Follow?> {
        return socialRepo.getMembers()
    }
}