package com.habitude.habit.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.EpoxyController
import com.habitude.habit.data.network.model.UiModels.HomePageModels.Action
import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.habitude.habit.domain.models.HabitThumb
import com.habitude.habit.ui.model.Epoxy.HabitItemEpoxyModel
import com.habitude.habit.ui.model.Epoxy.HeaderSectionEpoxyModel
import com.habitude.habit.ui.model.Epoxy.NavSectionEpoxyModel
import com.habitude.habit.ui.model.Epoxy.ProgressSectionHabit
import com.habitude.habit.ui.model.Epoxy.QuoteScrollerEpoxyModel
import com.habitude.habit.ui.model.Epoxy.UserInfoSectionEpoxyModel
import com.habitude.habit.ui.model.Epoxy.UserProgressSectionEpoxyModel
import kotlinx.coroutines.CoroutineScope

class HomePageEpoxyRecycler(
    val totalHabits: Int,
    val completedHabits: Int,
    val habits: MutableList<ProgressSectionHabit>,
    val onClick:(action:Action,avatarUrl:String?) -> Unit
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
                    NavSectionEpoxyModel(it){ onClick(it,null) }.id(it.id).addTo(this)
                }
                is HomeElements.UserInfoSection -> {
                    UserInfoSectionEpoxyModel(it){it,avatarUrl ->
                        onClick(it,avatarUrl)
                    }.id(it.id).addTo(this)

                }

                is HomeElements.HeaderSection -> {
                    HeaderSectionEpoxyModel(it).id(it.id).addTo(this)
                }
                is HomeElements.NavSectionItem -> {}
                is HomeElements.QuoteCarousalSection -> {
                    Log.e("TAG", "buildModels: $it", )
                    QuoteScrollerEpoxyModel(it).id(it.id).addTo(this)
                }
                is HomeElements.UserProgressSection -> {
                    val progresss = it
                    if(totalHabits>0) {
                        buildUserProgressSection(
                            progresss,
                            totalHabits,
                            completedHabits,
                            habits,
                            this
                        )
                    }



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
                        ){habitTitle ->
                            onClick(object : Action {
                                override val actionType: String
                                    get() = "open_screen"
                                override val resId: String?
                                    get() = habitTitle
                                override val screenType: String
                                    get() = "add_habit"

                            },null)
                        }.id(habit.habitName)
                    }
                    val carousal = CarouselModel_()
                    carousal.hasFixedSize(true)

                    CarouselModel_().paddingDp(20).models(modelList)
                        .id(it.id).addTo(this)




                }
                else -> {}
            }
        }
    }



    private fun buildUserProgressSection(
        progresss: HomeElements.UserProgressSection,
        totalHabits: Int,
        completedHabits: Int,
        habits: MutableList<ProgressSectionHabit>,
        context: HomePageEpoxyRecycler
    ) {
        UserProgressSectionEpoxyModel(progresss,totalHabits,completedHabits,habits).id(progresss.id).addTo(context)
    }
}