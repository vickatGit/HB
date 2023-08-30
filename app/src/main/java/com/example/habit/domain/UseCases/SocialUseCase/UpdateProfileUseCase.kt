package com.example.habit.domain.UseCases.SocialUseCase

import com.example.habit.domain.Repository.SocialRepo
import com.example.habit.domain.models.User.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(user:User): Flow<Boolean> {
        return socialRepo.updateUserProfile(user)
    }
}