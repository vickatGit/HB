package com.habitude.habit.domain.UseCases.SocialUseCase

import com.habitude.habit.domain.Repository.SocialRepo
import com.habitude.habit.domain.models.User.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    operator fun invoke(userId: String): Flow<User?> {
        return socialRepo.getUserProfile(userId)
    }
}