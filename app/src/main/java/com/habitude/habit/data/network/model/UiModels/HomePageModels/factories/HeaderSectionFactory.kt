package com.habitude.habit.data.network.model.UiModels.HomePageModels.factories

import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface HeaderSectionFactory{
    fun create(header:JsonObject):HomeElements
}
class HeaderSectionFactoryImpl @Inject constructor(
    private val typographyFactory: TypographyFactory
):HeaderSectionFactory {
    override fun create(header: JsonObject):HomeElements {
        return HomeElements.HeaderSection(
            header.get("id").asString,
            header.get("sectionType").asString,
            header.get("paddingTop").asString.toFloat(),
            header.get("paddingBottom").asString.toFloat(),
            header.get("paddingLeft").asString.toFloat(),
            header.get("paddingRight").asString.toFloat(),
            header.get("marginTop").asString.toFloat(),
            header.get("marginBottom").asString.toFloat(),
            header.get("marginLeft").asString.toFloat(),
            header.get("marginRight").asString.toFloat(),
            typographyFactory.create(header.get("element").asJsonObject) as HomeElements.Typography,
        )
    }
}