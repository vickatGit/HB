package com.example.habit.ui.adapter

import android.util.Log
import com.airbnb.epoxy.EpoxyController
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.ui.model.Epoxy.NavSectionEpoxyModel
import kotlin.math.log

class HomePageEpoxyRecycler : EpoxyController() {
     var homeSections:List<HomeElements> = emptyList()
        set(value){
            field=value
            requestModelBuild()
        }
    override fun buildModels() {
        Log.e("TAG", "buildModels: ", )
        homeSections.forEach {
            Log.e("TAG", "buildModels: type ${it.sectionType} ", )
            when(it){
                is HomeElements.NavSection -> {
                    NavSectionEpoxyModel(it){  }.id(it.id).addTo(this)
                }

                is HomeElements.HeaderSection -> {}
                is HomeElements.NavSectionItem -> {}
                is HomeElements.QuoteCarousalSection -> {}
                is HomeElements.QuoteSection -> {}
                is HomeElements.UserInfoSection -> {}
                is HomeElements.UserProgressSection -> {}
                else -> {}
            }
        }
    }
}