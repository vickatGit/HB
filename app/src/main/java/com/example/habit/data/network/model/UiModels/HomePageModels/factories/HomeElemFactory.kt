package com.example.habit.data.network.model.UiModels.HomePageModels.factories

import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface HomeElemFactory{
    fun create(type:JsonObject): HomeElements?
}
class HomeElemFactoryImpl @Inject constructor():HomeElemFactory{
    override fun create(type: JsonObject): HomeElements? {
        return when(type.get("id").asString){
            "Typography" -> {
                HomeElements.Typography(
                    type.get("id").asString,
                    type.get("sectionType").asString,
                    type.get("headerText")?.asString,
                    type.get("headerTextSize").asFloat,
                    type.get("headerTextStyle").asString,
                    type.get("verticalPosition").asString,
                    type.get("horizontalPosition").asString,
                    type.get("headerFont")?.asString,
                    type.get("textColor").asString,
                )
            }
            "UserProfileImage" -> {
                HomeElements.UserProfileImage(
                    type.get("id").asString,
                    type.get("sectionType").asString,
                    type.get("url")?.asString,
                    type.get("shape").asString,
                    type.get("sizeInDp").asFloat,
                    type.get("verticalPosition").asString,
                    type.get("horizontalPosition").asString,
                    type.get("cornerRadius").asFloat,
                )
            }


            else -> {
                null
            }
        }
    }
}