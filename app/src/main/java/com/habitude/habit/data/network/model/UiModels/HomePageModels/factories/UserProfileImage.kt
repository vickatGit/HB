package com.habitude.habit.data.network.model.UiModels.HomePageModels.factories

import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface UserProfileImageFactory{
    fun create(profile: JsonObject):HomeElements
}
class UserProfileImageFactoryImpl @Inject constructor(
    private val actionFactory: ActionFactory
):UserProfileImageFactory {
    override fun create(profile: JsonObject): HomeElements {
        return HomeElements.UserProfileImage(
            profile.get("id").asString,
            profile.get("sectionType").asString,
            profile.get("url").asString,
            profile.get("shape").asString,
            profile.get("sizeInDp").asFloat,
            profile.get("verticalPosition").asString,
            profile.get("horizontalPosition").asString,
            profile.get("cornerRadius").asFloat,
            actionFactory.create(profile.get("action")?.asJsonObject)
        )
    }
}