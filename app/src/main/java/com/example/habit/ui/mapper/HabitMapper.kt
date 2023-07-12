package com.example.habit.ui.mapper

import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import com.example.habit.ui.model.HabitThumbView
import com.example.habit.ui.model.HabitView

class HabitMapper : Mapper<HabitView,Habit,HabitThumb,HabitThumbView> {
    override fun mapFromHabit(type: HabitView): Habit {
        return Habit(
            type.id,
            type.title,
            type.description,
            type.reminderQuestion,
            type.startDate,
            type.endDate,
            type.isReminderOn,
            type.reminderTime
        )
    }

    override fun mapToHabit(type: Habit): HabitView {
        return HabitView(
            type.id,
            type.title,
            type.description,
            type.reminderQuestion,
            type.startDate,
            type.endDate,
            type.isReminderOn,
            type.reminderTime

        )
    }

    override fun mapToHabitThumbView(type: HabitThumb): HabitThumbView {
        return HabitThumbView(
            type.id,
            type.title,
            type.startDate,
            type.endDate,
        )
    }
}