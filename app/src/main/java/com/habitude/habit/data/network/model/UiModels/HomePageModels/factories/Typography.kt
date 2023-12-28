package com.habitude.habit.data.network.model.UiModels.HomePageModels.factories

import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface TypographyFactory{
    fun create(typo:JsonObject):HomeElements
}
class TypographyFactoryImpl @Inject constructor():TypographyFactory {
    override fun create(typo: JsonObject): HomeElements {
        return HomeElements.Typography(
            typo.get("id").asString,
            typo.get("sectionType").asString,
            typo.get("headerText")?.asString,
            typo.get("headerTextSize").asFloat,
            typo.get("headerTextStyle").asString,
            typo.get("verticalPosition").asString,
            typo.get("horizontalPosition").asString,
            typo.get("headerFont")?.asString,
            typo.get("textColor").asString,
        )
    }
}