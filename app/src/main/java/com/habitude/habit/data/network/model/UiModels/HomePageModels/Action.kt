package com.habitude.habit.data.network.model.UiModels.HomePageModels

interface Action {
    val actionType:String
    val resId:String?
    val screenType:String
}