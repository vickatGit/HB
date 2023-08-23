package com.example.habit.domain.UseCases.SocialUseCase

import com.example.habit.data.network.model.UsersModel.User
import com.example.habit.domain.Repository.SocialRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersByUserNameUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    operator fun invoke(query:String): Flow<List<User>> {
        return socialRepo.getUsersByUsername(query)
    }
}