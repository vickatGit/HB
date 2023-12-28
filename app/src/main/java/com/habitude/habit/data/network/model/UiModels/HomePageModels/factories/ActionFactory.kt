package com.habitude.habit.data.network.model.UiModels.HomePageModels.factories

import com.habitude.habit.data.network.model.UiModels.HomePageModels.Action
import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeActionScreen
import com.google.gson.JsonObject
import javax.inject.Inject

interface ActionFactory{
    fun create(type: JsonObject?):Action?
}
class ActionFactoryImpl @Inject constructor():ActionFactory {
    override fun create(type: JsonObject?): Action? {
        type?.let {
            return HomeActionScreen(
                type.get("actionType").asString,
                type.get("resId")?.asString ?: null,
                type.get("screenType").asString,
            )
        }
        return null
    }
}