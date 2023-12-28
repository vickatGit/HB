package com.habitude.habit.domain.UseCases.SocialUseCase

import com.habitude.habit.domain.Repository.SocialRepo
import com.habitude.habit.domain.models.Follow.Follow
import com.habitude.habit.domain.models.User.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFollowingsUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(): Flow<Follow?> {
        return socialRepo.getFollowings()
    }
}