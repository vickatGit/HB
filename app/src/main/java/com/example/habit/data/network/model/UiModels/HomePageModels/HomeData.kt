package com.example.habit.data.network.model.UiModels.HomePageModels

import kotlinx.serialization.Serializable

@Serializable
data class HomeData(
    val data:Sections
)
@Serializable
data class Sections(
    val sections:List<HomeElements>
)
