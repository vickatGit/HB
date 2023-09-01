package com.example.habit.data.network.model.UiModels.HomePageModels.factories

import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface NavSectionFactory{
    fun create(type:JsonObject):HomeElements.NavSection
}
class NavSectionFactoryImpl @Inject constructor(
    private val navItemFactory: NavItemFactory
) : NavSectionFactory {
    override fun create(type: JsonObject): HomeElements.NavSection {
        return HomeElements.NavSection(
            type.get("id").asString,
            type.get("sectionType").asString,
            type.getAsJsonArray("navItems").map {
                navItemFactory.create(it.asJsonObject)
            },
            type.get("paddingTop").asString.toFloat(),
            type.get("paddingBottom").asString.toFloat(),
            type.get("paddingLeft").asString.toFloat(),
            type.get("paddingRight").asString.toFloat(),
            type.get("marginTop").asString.toFloat(),
            type.get("marginBottom").asString.toFloat(),
            type.get("marginLeft").asString.toFloat(),
            type.get("marginRight").asString.toFloat(),
        )
    }

}