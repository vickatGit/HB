package com.habitude.habit.data.network.model.UiModels.HomePageModels.factories

import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface UserInfoSectionFactory {
    fun create(section: JsonObject): HomeElements
}

class UserInfoSectionFactoryImpl @Inject constructor(
    private val homeElemFactory: HomeElemFactory

) : UserInfoSectionFactory {
    override fun create(section: JsonObject): HomeElements {
        return HomeElements.UserInfoSection(
            section.get("id").asString,
            section.get("sectionType").asString,
            section.getAsJsonArray("elements").map {
                it?.asJsonObject?.let { homeElemFactory.
                create(it) }
            },
            section.get("paddingTop").asString.toFloat(),
            section.get("paddingBottom").asString.toFloat(),
            section.get("paddingLeft").asString.toFloat(),
            section.get("paddingRight").asString.toFloat(),
            section.get("marginTop").asString.toFloat(),
            section.get("marginBottom").asString.toFloat(),
            section.get("marginLeft").asString.toFloat(),
            section.get("marginRight").asString.toFloat(),
        )

    }
}