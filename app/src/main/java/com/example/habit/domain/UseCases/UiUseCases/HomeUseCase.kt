package com.example.habit.domain.UseCases.UiUseCases

import com.example.habit.data.network.model.UiModels.HomePageModels.HomeData
import com.example.habit.domain.Repository.SocialRepo
import javax.inject.Inject

class HomeUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(): HomeData? {
        return socialRepo.getHomeData()
    }
}