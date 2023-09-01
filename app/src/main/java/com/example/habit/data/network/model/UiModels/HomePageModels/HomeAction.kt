package com.example.habit.data.network.model.UiModels.HomePageModels

import kotlinx.serialization.Serializable

@Serializable
data class HomeActionScreen(
    override val actionType: String,
    override val resId: String,
    override val screenType: String
):Action
