package com.example.habit.ui.mapper.GroupHabitMapper

import com.example.habit.domain.models.GroupHabit
import com.example.habit.ui.model.GroupHabitView
import com.example.habit.ui.model.HabitView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GroupHabitMapper : GroupHabitMapperI<GroupHabitView,GroupHabit,HabitView> {
    override fun toGroupHabit(type: GroupHabitView): GroupHabit {
        return GroupHabit(
            type.id,
            type.serverId,
            type.title,
            type.description,
            type.reminderQuestion,
            type.startDate,
            type.endDate,
            type.isReminderOn,
            type.reminderTime

        )
    }

    override fun toGroupHabitView(type: GroupHabit): GroupHabitView {
        return GroupHabitView(
            type.id,
            type.serverId,
            type.title,
            type.description,
            type.reminderQuestion,
            type.startDate!!,
            type.endDate!!,
            type.isReminderOn,
            type.reminderTime

        )
    }

    override fun toGroupHabitFromHabit(type: HabitView): GroupHabit {
        return GroupHabit(
            type.id,
            type.serverId,
            type.title,
            type.description,
            type.reminderQuestion,
            type.startDate,
            type.endDate,
            type.isReminderOn,
            type.reminderTime
        )
    }

}