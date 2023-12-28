package com.habitude.habit.domain.UseCases.UiUseCases

import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeData
import com.habitude.habit.domain.Repository.SocialRepo
import javax.inject.Inject

class HomeUseCase @Inject constructor(
    private val socialRepo: SocialRepo
) {
    suspend operator fun invoke(): HomeData? {
        return socialRepo.getHomeData()
    }
}