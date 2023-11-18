package com.example.habit.domain.UseCases.SocialUseCase

import com.example.habit.domain.Repository.SocialRepo
import com.example.habit.domain.models.User.User
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import javax.inject.Inject


class UploadAvatarUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(requestBody: RequestBody): Flow<Boolean> {
        return socialRepo.uploadUserAvatar(requestBody)
    }
}