package com.example.habit.data.network.model.UiModels.HomePageModels.factories

import android.util.Log
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.google.gson.JsonArray
import javax.inject.Inject

interface HomeSectionsFactory{
    fun create(types: JsonArray?):List<HomeElements>
}

class HomeSectionsFactoryImpl @Inject constructor(
    private val navSectionFactory:NavSectionFactory,
    private val userInfoSectionFactory: UserInfoSectionFactory,
    private val headerSectionFactory: HeaderSectionFactory,
    private val qouteCarousalSectionFactory: QouteCarousalSectionFactory,
    private val userProgressSectionFactory: UserProgressSectionFactory
):HomeSectionsFactory {
    override fun create(sections: JsonArray?): List<HomeElements> {
        val homeElements= mutableListOf<HomeElements>()
        sections?.forEach { section ->
            val section=section.asJsonObject
            Log.e("TAG", "getHome section: ${section.get("sectionType").toString()}", )
            when(section.get("sectionType").asString){
                "NavSection" -> {
                    Log.e("TAG", "getHome navsection:", )
                    homeElements.add(navSectionFactory.create(section))
                }
                "UserInfoSection" -> {
                    homeElements.add(userInfoSectionFactory.create(section))
                }
                "HeaderSection" -> {
                   homeElements.add(headerSectionFactory.create(section))
                }
                "QouteCarousalSection" -> {
                    homeElements.add(qouteCarousalSectionFactory.create(section))
                }
                "UserProgressSection" -> {
                    homeElements.add(userProgressSectionFactory.create(section))
                }
            }
        }
        return homeElements
    }

}