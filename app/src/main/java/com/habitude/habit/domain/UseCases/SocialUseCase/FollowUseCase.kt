package com.habitude.habit.domain.UseCases.SocialUseCase

import com.habitude.habit.domain.Repository.SocialRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FollowUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(friendId:String): Flow<Boolean> {
        return socialRepo.followUser(friendId)
    }
}