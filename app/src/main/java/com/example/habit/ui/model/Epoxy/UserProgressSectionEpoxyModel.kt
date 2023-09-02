package com.example.habit.ui.model.Epoxy

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.Typeface
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habit.R
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.databinding.UserProgressSectionLayoutBinding
import com.example.habit.domain.UseCases.HabitUseCase.GetHabitThumbsUseCase
import com.example.habit.ui.adapter.SectionHabitsAdapter
import com.example.habit.ui.util.DpPxUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneOffset

data class UserProgressSectionEpoxyModel (
    private val getHabits: GetHabitThumbsUseCase,
    private val progressSection: HomeElements.UserProgressSection
) : ViewBindingKotlinModel<UserProgressSectionLayoutBinding>(R.layout.user_progress_section_layout) {
    override fun UserProgressSectionLayoutBinding.bind() {
        userProgressSection.layoutParams.apply {
            this as RecyclerView.LayoutParams
            setMargins(
                DpPxUtils.dpToPX(progressSection.marginLeft,userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.marginTop,userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.marginRight,userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.marginBottom,userProgressSection.context),
            )
            userProgressSection.setPadding(
                DpPxUtils.dpToPX(progressSection.paddingLeft,userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.paddingTop,userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.paddingRight,userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.paddingBottom,userProgressSection.context),
            )
        }

        if(progressSection.isAnimationOn) waveView.setAnimationSpeed(progressSection.animationSpeed.toInt()) else waveView.stopAnimation()
        waveView.setFrontWaveColor(Color.parseColor(progressSection.progressPrimaryColor))
        waveView.setBehindWaveColor(Color.parseColor(progressSection.progressSecondaryColor))
        view.background.setColorFilter(Color.parseColor(progressSection.progressBackgroundColor),PorterDuff.Mode.SRC_ATOP)
        view.layoutParams.apply {
            width=DpPxUtils.dpToPX(progressSection.progressSize,view.context)
            height=DpPxUtils.dpToPX(progressSection.progressSize,view.context)
        }

        totalProgress.setTextColor(Color.parseColor(progressSection.progressTypographyProperties.textColor))
        totalProgress.textSize=progressSection.progressTypographyProperties.headerTextSize
        val userTextStyle=
            if(progressSection.progressTypographyProperties.headerTextStyle.toLowerCase()=="bold") Typeface.BOLD
            else if(progressSection.progressTypographyProperties.headerTextStyle.toLowerCase()=="normal") Typeface.NORMAL
            else if(progressSection.progressTypographyProperties.headerTextStyle.toLowerCase()=="italic") Typeface.ITALIC
            else if(progressSection.progressTypographyProperties.headerTextStyle.toLowerCase()=="bold_italic") Typeface.BOLD_ITALIC
            else Typeface.NORMAL
        totalProgress.typeface= Typeface.create(totalProgress.typeface,userTextStyle)

        CoroutineScope(Dispatchers.IO).launch {
            getHabits(this).collectLatest {
                val habits = mutableListOf<ProgressSectionHabit>()
                var totalHabits = 0
                var completedHabits = 0
                it.forEach {habit ->
                    if(habit.entries!=null) {
                        if(habit.entries!!.isEmpty()) {
                            totalHabits++
                            habits.add(ProgressSectionHabit(habit.title!!,false))
                        }
                        habit.entries?.forEach {
                            if (it.key == LocalDate.now()) {
                                totalHabits++
                                if(it.value.completed) ++completedHabits
                                habits.add(ProgressSectionHabit(habit.title!!,it.value.completed))
                            }
                        }
                    }else{
                        totalHabits++
                    }
                }
                waveView.progress=((completedHabits.toFloat() / totalHabits.toFloat()) * 1000f).toInt()
                withContext(Dispatchers.Main){
                    habitsRecycler.layoutManager=LinearLayoutManager(habitsRecycler.context)
                    val habitsAdapter = SectionHabitsAdapter(habits,progressSection.progressTypographyProperties)
                    habitsRecycler.adapter=habitsAdapter
                }

            }
        }
    }

}

data class ProgressSectionHabit(
    val habitName: String,
    val isCompleted: Boolean
)
fun LocalDate.toMilliseconds(): Long {
    return this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}