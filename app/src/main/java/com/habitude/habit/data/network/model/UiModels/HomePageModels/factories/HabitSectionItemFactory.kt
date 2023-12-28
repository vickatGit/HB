package com.habitude.habit.data.network.model.UiModels.HomePageModels.factories

import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface HabitSectionItemFactory{
    fun create(type:JsonObject):HomeElements
}
class HabitSectionItemFactoryImpl @Inject constructor():HabitSectionItemFactory {
    override fun create(type: JsonObject): HomeElements {
        return HomeElements.HabitsSectionItem(
            type.get("id").asString,
            type.get("sectionType").asString,
            type.get("habitName").asString,
            type.get("habitThumbnail").asString,
        )
    }
}