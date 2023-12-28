package com.habitude.habit.domain.UseCases.SocialUseCase

import com.habitude.habit.domain.Repository.SocialRepo
import com.habitude.habit.domain.models.Follow.Follow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMembersUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    @Throws
    suspend operator fun invoke(): Flow<Follow?> {
        return socialRepo.getMembers()
    }
}