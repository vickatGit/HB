package com.example.habit.ui.adapter

import android.util.Log
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.EpoxyController
import com.example.habit.data.network.model.UiModels.HomePageModels.Action
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.domain.UseCases.HabitUseCase.GetHabitThumbsUseCase
import com.example.habit.ui.model.Epoxy.HabitItemEpoxyModel
import com.example.habit.ui.model.Epoxy.HeaderSectionEpoxyModel
import com.example.habit.ui.model.Epoxy.NavSectionEpoxyModel
import com.example.habit.ui.model.Epoxy.QuoteScrollerEpoxyModel
import com.example.habit.ui.model.Epoxy.UserInfoSectionEpoxyModel
import com.example.habit.ui.model.Epoxy.UserProgressSectionEpoxyModel
import kotlinx.coroutines.CoroutineScope

class HomePageEpoxyRecycler(
    private val getHabitThumbsUseCase: GetHabitThumbsUseCase,
    private val uiScope:CoroutineScope,
    val onClick:(action:Action) -> Unit
) : EpoxyController() {
     var homeSections:List<HomeElements> = emptyList()
        set(value){
            field=value
            requestModelBuild()
        }
    override fun buildModels() {
        Log.e("TAG", "buildModels: ", )
        homeSections.forEach {
//            Log.e("TAG", "buildModels: type ${it.sectionType} ", )
            when(it){
                is HomeElements.NavSection -> {
                    NavSectionEpoxyModel(it){ onClick(it) }.id(it.id).addTo(this)
                }
                is HomeElements.UserInfoSection -> {
                    UserInfoSectionEpoxyModel(it){ onClick(it) }.id(it.id).addTo(this)
                }

                is HomeElements.HeaderSection -> {
                    HeaderSectionEpoxyModel(it).id(it.id).addTo(this)
                }
                is HomeElements.NavSectionItem -> {}
                is HomeElements.QuoteCarousalSection -> {
                    Log.e("TAG", "buildModels: $it", )
                    QuoteScrollerEpoxyModel(it,uiScope).id(it.id).addTo(this)
                }
                is HomeElements.UserProgressSection -> {
                    UserProgressSectionEpoxyModel(getHabitThumbsUseCase,it).id(it.id).addTo(this)
                }
                is HomeElements.HabitCarousalSection -> {
                    val modelList = it.habits.map { habit ->
                        HabitItemEpoxyModel(
                            it.habitTitleProperties,
                            habit,
                            it.marginLeft,
                            it.marginRight,
                            it.marginTop,
                            it.marginBottom,
                            it.paddingLeft,
                            it.paddingRight,
                            it.paddingTop,
                            it.paddingBottom,
                            it.imageCornerRadius,
                            it.imageHeight,
                            it.width,
                            it.height
                        ){}.id(habit.habitName)
                    }
                    val carousal = CarouselModel_()

                    CarouselModel_().paddingDp(20).models(modelList)
                        .id(it.id).addTo(this)



                }
                else -> {}
            }
        }
    }
}