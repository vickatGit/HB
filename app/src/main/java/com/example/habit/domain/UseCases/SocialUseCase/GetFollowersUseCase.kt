package com.example.habit.domain.UseCases.SocialUseCase

import com.example.habit.domain.Repository.SocialRepo
import com.example.habit.domain.models.Follow.Follow
import com.example.habit.domain.models.User.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFollowersUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(): Flow<Follow?> {
        return socialRepo.getFollowers()
    }
}