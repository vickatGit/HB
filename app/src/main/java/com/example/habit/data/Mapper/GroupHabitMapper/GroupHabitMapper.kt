package com.example.habit.data.Mapper.GroupHabitMapper

import com.example.habit.data.Mapper.HabitMapper.HabitMapper
import com.example.habit.data.local.entity.GroupHabitsEntity
import com.example.habit.data.local.entity.HabitGroupWithHabitsEntity
import com.example.habit.data.network.model.GroupHabitModel.GroupHabitModel
import com.example.habit.data.network.model.GroupHabitModel.MemberModel
import com.example.habit.data.util.HabitGroupRecordSyncType
import com.example.habit.data.util.HabitRecordSyncType
import com.example.habit.domain.models.GroupHabit
import com.example.habit.domain.models.GroupHabitWithHabits
import com.example.habit.domain.models.Member
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class GroupHabitMapper @Inject constructor(
    private val habitMapper: HabitMapper
) : GroupHabitMapperI<GroupHabitModel,GroupHabitsEntity,GroupHabit,GroupHabitWithHabits,HabitGroupWithHabitsEntity> {
    override fun toGroupHabitEntity(type: GroupHabitsEntity): GroupHabitModel {
        return GroupHabitModel(
            type.serverId,
            type.localId,
            type.description,
            type.title!!,
            type.startDate.toString(),
            type.endDate.toString(),
            type.isReminderOn,
            type.reminderQuestion!!,
            type.reminderTime.toString()
        )
    }

    override fun toGroupHabitModel(type: GroupHabitModel): GroupHabitsEntity {
        return GroupHabitsEntity(
            type.localId!!,
            type.id,
            type.title,
            type.description,
            type.reminderQuestion,
            localDateConverter(type.startDate.toString()),
            localDateConverter(type.endDate.toString()),
            type.isReminderOn,
            localDateTimeConverter(type.reminderTime.toString()),
            Gson().toJson(type.members)
        )
    }
    private fun localDateConverter(date:String): LocalDate? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            LocalDate.parse(date, formatter)
        } catch (e: Exception) {
            System.err.println("Error parsing the date-time string: " + e.message)
            null
        }
    }

    private fun localDateTimeConverter(date:String): LocalDateTime? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            LocalDateTime.parse(date, formatter)
        } catch (e: java.lang.Exception) {
            System.err.println("Error parsing the date-time string: " + e.message)
            null
        }

    }

    override fun fromGroupHabit(type: GroupHabit,syncType: HabitGroupRecordSyncType): GroupHabitsEntity {
        return GroupHabitsEntity(
            type.id,
            type.serverId,
            type.title,
            type.description,
            type.reminderQuestion,
            type.startDate,
            type.endDate,
            type.isReminderOn,
            type.reminderTime,
            Gson().toJson(type.members)?:"",
            syncType
        )
    }

    override fun toGroupHabitWithHabits(type: HabitGroupWithHabitsEntity): GroupHabitWithHabits {
        return GroupHabitWithHabits(
            toGroupHabit(type.habitGroup),
            type.habits.map {
                habitMapper.mapToHabit(it)
            }
        )
    }

    override fun toGroupHabit(type: GroupHabitsEntity): GroupHabit {
        val memberListType = object : TypeToken<List<Member>>() {}.type
        val members : List<Member> = Gson().fromJson(type.members,memberListType)
        return GroupHabit(
            type.localId,
            type.serverId,
            type.title,
            type.description,
            type.reminderQuestion,
            type.startDate,
            type.endDate,
            type.isReminderOn,
            type.reminderTime,
            members

        )
    }
}