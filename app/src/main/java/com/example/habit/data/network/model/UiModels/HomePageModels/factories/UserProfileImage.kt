package com.example.habit.data.network.model.UiModels.HomePageModels.factories

import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonObject
import javax.inject.Inject

interface UserProfileImageFactory{
    fun create(profile: JsonObject):HomeElements
}
class UserProfileImageFactoryImpl @Inject constructor():UserProfileImageFactory {
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
        )
    }
}