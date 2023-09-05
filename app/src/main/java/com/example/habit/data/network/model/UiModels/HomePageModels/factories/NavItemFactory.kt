package com.example.habit.data.network.model.UiModels.HomePageModels.factories

import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface NavItemFactory{
    fun create(type: JsonObject): HomeElements.NavSectionItem
}
class NavItemFactoryImpl @Inject constructor(
    private val actionFactory: ActionFactory
):NavItemFactory {
    override fun create(type: JsonObject): HomeElements.NavSectionItem {
        return HomeElements.NavSectionItem(
            type.get("id").asString,
            type.get("type").asString,
            type.get("icon").asString,
            type.get("iconSizeInDp").asString.toFloat(),
            type.get("iconColor").asString,
            type.get("iconVerticalPosition").asString,
            type.get("iconHorizontalPosition").asString,
            actionFactory.create(type.get("action")?.asJsonObject)
        )
    }
}