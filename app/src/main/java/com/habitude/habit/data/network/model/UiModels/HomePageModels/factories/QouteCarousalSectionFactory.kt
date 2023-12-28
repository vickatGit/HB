package com.habitude.habit.data.network.model.UiModels.HomePageModels.factories

import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface QouteCarousalSectionFactory{
    fun create(section: JsonObject):HomeElements
}
class QouteCarousalSectionFactoryImpl @Inject constructor():QouteCarousalSectionFactory {
    override fun create(section: JsonObject): HomeElements {
        return HomeElements.QuoteCarousalSection(
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
            section.get("images").asJsonArray.map { it.asString },
            section.get("imageCornerRadius").asString.toFloat(),
            section.get("imageHeight").asString.toFloat(),
        )
    }
}