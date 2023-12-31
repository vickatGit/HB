package com.habitude.habit.domain.UseCases.SocialUseCase

import com.habitude.habit.domain.Repository.SocialRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsUserFollowingUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    operator fun invoke(friendId:String): Flow<Boolean?>{
        return socialRepo.isUserFollowing(friendId)
    }
}