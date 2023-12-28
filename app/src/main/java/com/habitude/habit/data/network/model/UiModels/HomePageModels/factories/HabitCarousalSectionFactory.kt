package com.habitude.habit.data.network.model.UiModels.HomePageModels.factories

import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface HabitCarousalSectionFactory{
    fun create(type:JsonObject):HomeElements
}
class HabitCarousalSectionFactoryImpl @Inject constructor(
    private val habitSectionItemFactory: HabitSectionItemFactory,
    private val typographyFactory: TypographyFactory,
    private val actionFactory: ActionFactory
):HabitCarousalSectionFactory {
    override fun create(type: JsonObject): HomeElements {
        return HomeElements.HabitCarousalSection(
            type.get("id").asString,
            type.get("sectionType").asString,
            type.get("paddingTop").asString.toFloat(),
            type.get("paddingBottom").asString.toFloat(),
            type.get("paddingLeft").asString.toFloat(),
            type.get("paddingRight").asString.toFloat(),
            type.get("marginTop").asString.toFloat(),
            type.get("marginBottom").asString.toFloat(),
            type.get("marginLeft").asString.toFloat(),
            type.get("marginRight").asString.toFloat(),
            type.get("imageCornerRadius").asString.toFloat(),
            type.get("imageHeight").asString.toFloat(),
            type.get("width")?.asString,
            type.get("height")?.asString,
            typographyFactory.create(type.get("habitTitleProperties").asJsonObject) as HomeElements.Typography,
            type.get("habits").asJsonArray.map {
                habitSectionItemFactory.create(it.asJsonObject) as HomeElements.HabitsSectionItem
            },
            actionFactory.create(type.get("action")?.asJsonObject)


        )
    }
}