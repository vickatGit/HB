package com.habitude.habit.domain.UseCases.SocialUseCase

import com.habitude.habit.data.network.model.UsersModel.UserModel
import com.habitude.habit.domain.Repository.SocialRepo
import com.habitude.habit.domain.models.User.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersByUserNameUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    operator fun invoke(query:String): Flow<List<User>> {
        return socialRepo.getUsersByUsername(query)
    }
}