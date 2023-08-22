package com.example.habit.ui.mapper.GroupHabitMapper

import com.example.habit.domain.models.GroupHabit
import com.example.habit.domain.models.GroupHabitWithHabits
import com.example.habit.domain.models.Member
import com.example.habit.ui.mapper.HabitMapper.HabitMapper
import com.example.habit.ui.model.GroupHabitView
import com.example.habit.ui.model.GroupHabitWithHabitsView
import com.example.habit.ui.model.HabitView
import com.example.habit.ui.model.MemberView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GroupHabitMapper @Inject constructor(
    private val habitMapper: HabitMapper
) : GroupHabitMapperI<GroupHabitView,GroupHabit,HabitView,GroupHabitWithHabits,GroupHabitWithHabitsView,Member,MemberView> {
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
            type.reminderTime,
            admin = type.admin

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
            type.reminderTime,
            type.members?.map {
                fromMember(it)
            },
            type.admin


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
            type.reminderTime,
        )
    }

    override fun fromGroupHabitsWithHabit(type: GroupHabitWithHabits): GroupHabitWithHabitsView {
        return GroupHabitWithHabitsView(
            toGroupHabitView(type.habitGroup),
            type.habits.map {
                habitMapper.mapToHabit(it)
            }
        )
    }

    override fun fromMember(type: Member): MemberView {
        return MemberView(
            type.userId,
            type.username
        )
    }

}