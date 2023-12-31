package com.habitude.habit.ui.model.Epoxy

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.habitude.habit.R
import com.habitude.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.habitude.habit.databinding.UserProgressSectionLayoutBinding
import com.habitude.habit.domain.UseCases.HabitUseCase.GetHabitThumbsUseCase
import com.habitude.habit.ui.adapter.SectionHabitsAdapter
import com.habitude.habit.ui.util.DpPxUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneOffset

data class UserProgressSectionEpoxyModel(
    private val progressSection: HomeElements.UserProgressSection,
    val totalHabits: Int,
    val completedHabits: Int,
    val habits: MutableList<ProgressSectionHabit>
) : ViewBindingKotlinModel<UserProgressSectionLayoutBinding>(R.layout.user_progress_section_layout) {
    override fun UserProgressSectionLayoutBinding.bind() {
        Log.e("TAG", "bind: getAllHabitsForProgress section Habits ${Gson().toJson(habits)}", )

        userProgressSection.layoutParams.apply {
            this as RecyclerView.LayoutParams
            setMargins(
                DpPxUtils.dpToPX(progressSection.marginLeft, userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.marginTop, userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.marginRight, userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.marginBottom, userProgressSection.context),
            )
            userProgressSection.setPadding(
                DpPxUtils.dpToPX(progressSection.paddingLeft, userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.paddingTop, userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.paddingRight, userProgressSection.context),
                DpPxUtils.dpToPX(progressSection.paddingBottom, userProgressSection.context),
            )
        }

        if (progressSection.isAnimationOn) waveView.setAnimationSpeed(progressSection.animationSpeed.toInt()) else waveView.stopAnimation()
        waveView.setFrontWaveColor(Color.parseColor(progressSection.progressPrimaryColor))
        waveView.setBehindWaveColor(Color.parseColor(progressSection.progressSecondaryColor))
        view.background.setColorFilter(
            Color.parseColor(progressSection.progressBackgroundColor),
            PorterDuff.Mode.SRC_ATOP
        )
        view.layoutParams.apply {
            width = DpPxUtils.dpToPX(progressSection.progressSize, view.context)
            height = DpPxUtils.dpToPX(progressSection.progressSize, view.context)
        }

        totalProgress.setTextColor(Color.parseColor(progressSection.progressTypographyProperties.textColor))
        totalProgress.textSize = progressSection.progressTypographyProperties.headerTextSize
        val userTextStyle =
            if (progressSection.progressTypographyProperties.headerTextStyle.toLowerCase() == "bold") Typeface.BOLD
            else if (progressSection.progressTypographyProperties.headerTextStyle.toLowerCase() == "normal") Typeface.NORMAL
            else if (progressSection.progressTypographyProperties.headerTextStyle.toLowerCase() == "italic") Typeface.ITALIC
            else if (progressSection.progressTypographyProperties.headerTextStyle.toLowerCase() == "bold_italic") Typeface.BOLD_ITALIC
            else Typeface.NORMAL
        totalProgress.typeface = Typeface.create(totalProgress.typeface, userTextStyle)


        waveView.progress =
            ((completedHabits.toFloat() / totalHabits.toFloat()) * 1000f).toInt()

        habitsRecycler.layoutManager = LinearLayoutManager(habitsRecycler.context)
        Log.e("TAG", "bind: getAllHabitsForProgress section Habits ${Gson().toJson(habits)}", )
        val habitsAdapter = SectionHabitsAdapter(
            habits,
            progressSection.progressTypographyProperties
        )
        habitsRecycler.adapter = habitsAdapter


    }
}


data class ProgressSectionHabit(
    val habitName: String,
    val isCompleted: Boolean
)

fun LocalDate.toMilliseconds(): Long {
    return this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}