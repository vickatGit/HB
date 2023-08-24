package com.example.habit.domain.UseCases.SocialUseCase

import com.example.habit.domain.Repository.SocialRepo
import javax.inject.Inject

class FollowUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(friendId:String){
        socialRepo.followUser(friendId)
    }
}