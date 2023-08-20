package com.example.habit.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupHabitWithHabitsView(
    val habitGroup:GroupHabitView,
    val habits : List<HabitView>
) : Parcelable{

}
