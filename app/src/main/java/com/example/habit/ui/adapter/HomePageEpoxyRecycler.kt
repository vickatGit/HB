package com.example.habit.ui.adapter

import android.util.Log
import com.airbnb.epoxy.EpoxyController
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.domain.UseCases.HabitUseCase.GetGroupHabitUseCase
import com.example.habit.domain.UseCases.HabitUseCase.GetHabitThumbsUseCase
import com.example.habit.ui.model.Epoxy.HeaderSectionEpoxyModel
import com.example.habit.ui.model.Epoxy.NavSectionEpoxyModel
import com.example.habit.ui.model.Epoxy.UserInfoSectionEpoxyModel
import com.example.habit.ui.model.Epoxy.UserProgressSectionEpoxyModel

class HomePageEpoxyRecycler(
    private val getHabitThumbsUseCase: GetHabitThumbsUseCase
) : EpoxyController() {
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
                is HomeElements.UserInfoSection -> {
                    UserInfoSectionEpoxyModel(it){ }.id(it.id).addTo(this)
                }

                is HomeElements.HeaderSection -> {
                    HeaderSectionEpoxyModel(it).id(it.id).addTo(this)
                }
                is HomeElements.NavSectionItem -> {}
                is HomeElements.QuoteCarousalSection -> {}
                is HomeElements.QuoteSection -> {}
                is HomeElements.UserProgressSection -> {
                    UserProgressSectionEpoxyModel(getHabitThumbsUseCase,it).id(it.id).addTo(this)
                }
                else -> {}
            }
        }
    }
}