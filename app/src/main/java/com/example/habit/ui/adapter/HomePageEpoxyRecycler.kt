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
import com.example.habit.ui.model.Epoxy.ProgressSectionHabit
import com.example.habit.ui.model.Epoxy.QuoteScrollerEpoxyModel
import com.example.habit.ui.model.Epoxy.UserInfoSectionEpoxyModel
import com.example.habit.ui.model.Epoxy.UserProgressSectionEpoxyModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

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
                    val progresss = it
                    CoroutineScope(Dispatchers.IO).launch {
                        getHabitThumbsUseCase(this).collectLatest {
                            val habits = mutableListOf<ProgressSectionHabit>()
                            var totalHabits = 0
                            var completedHabits = 0
                            it.forEach { habit ->
                                if (habit.entries != null) {
                                    if (habit.entries!!.isEmpty()) {
                                        totalHabits++
                                        habits.add(ProgressSectionHabit(habit.title!!, false))
                                    }
                                    habit.entries?.forEach {
                                        if (it.key == LocalDate.now()) {
                                            totalHabits++
                                            if (it.value.completed) ++completedHabits
                                            habits.add(
                                                ProgressSectionHabit(
                                                    habit.title!!,
                                                    it.value.completed
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    totalHabits++
                                }
                                withContext(Dispatchers.Main){
                                    UserProgressSectionEpoxyModel(progresss,totalHabits,completedHabits,habits).id(progresss.id).addTo(this@HomePageEpoxyRecycler)
                                }
                            }
                        }
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

                            })
                        }.id(habit.habitName)
                    }
                    CarouselModel_().paddingDp(20).models(modelList)
                        .id(it.id).addTo(this)



                }
                else -> {}
            }
        }
    }
}