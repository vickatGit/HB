package com.example.habit.data.network.model.UiModels.HomePageModels.factories

import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface UserProgressSectionFactory{
    fun create(section:JsonObject):HomeElements
}
class UserProgressSectionFactoryImpl @Inject constructor(
    private val typographyFactory: TypographyFactory
):UserProgressSectionFactory {
    override fun create(section: JsonObject): HomeElements {
        return HomeElements.UserProgressSection(
            section.get("id").asString,
            section.get("sectionType").asString,
            section.get("paddingTop").asString.toFloat(),
            section.get("paddingBottom").asString.toFloat(),
            section.get("paddingLeft").asString.toFloat(),
            section.get("paddingRight").asString.toFloat(),
            section.get("marginTop").asString.toFloat(),
            section.get("marginBottom").asString.toFloat(),
            section.get("marginLeft").asString.toFloat(),
            section.get("marginRight").asString.toFloat(),
            typographyFactory.create(section.get("progressTypographyProperties").asJsonObject) as HomeElements.Typography,
            section.get("progressType").asString,
            section.get("isAnimationOn").asBoolean,
            section.get("animationSpeed").asFloat,
            section.get("shouldShowProgressPercentage").asBoolean,
            section.get("progressPrimaryColor").asString,
            section.get("progressSecondaryColor").asString,
            typographyFactory.create(section.get("progressPercentageTypographyProperties").asJsonObject) as HomeElements.Typography,
            section.get("habitProgressType").asString,
            typographyFactory.create(section.get("habitPercentageTypographyProperties").asJsonObject) as HomeElements.Typography,
            section.get("progressSize").asFloat,
            section.get("progressBackgroundColor").asString,

        )
    }
}