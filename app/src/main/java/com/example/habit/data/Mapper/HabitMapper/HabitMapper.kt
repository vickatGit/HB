package com.example.habit.data.Mapper.HabitMapper

import com.example.habit.data.local.entity.EntryEntity
import com.example.habit.data.local.entity.HabitEntity
import com.example.habit.data.network.model.HabitsListModel.HabitModel
import com.example.habit.data.util.HabitRecordSyncType
import com.example.habit.domain.models.Entry
import com.example.habit.domain.models.Habit
import com.example.habit.domain.models.HabitThumb
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class HabitMapper @Inject constructor(private val entryMapper: EntryMapper) :
    HabitMapperI<HabitEntity?, HabitThumb, Habit, HabitModel> {
    override fun mapFromHabitEntity(type: HabitEntity?): HabitThumb {
        return HabitThumb(
            type?.id!!,
            type?.title,
            type?.startDate,
            type?.endDate,
            type.entryList?.let {
                it.mapValues {
                    entryMapper.mapToEntry(it.value)
                }.toMutableMap() as java.util.HashMap<LocalDate, Entry>?
            }

        )
    }


    override fun mapToHabitEntity(type: Habit, syncType: HabitRecordSyncType): HabitEntity {
        return HabitEntity(
            type.id,
            type.serverId,
            type.title,
            type.description,
            type.reminderQuestion,
            type.startDate,
            type.endDate,
            type.isReminderOn,
            type.reminderTime,
            type.entries?.let {
                it.mapValues {
                    entryMapper.mapFromEntry(it.value)
                }
            },
            syncType,
            type.habitGroupId,
            type.userId


        )
    }

    override fun mapToHabit(type: HabitEntity?): Habit {
        return Habit(
            type?.id!!,
            type.serverId,
            type?.title,
            type?.description,
            type?.reminderQuestion,
            type?.startDate,
            type?.endDate,
            type?.isReminderOn,
            type?.reminderTime,
            type?.entryList?.let {
                it.mapValues {
                    entryMapper.mapToEntry(it.value)
                } as HashMap<LocalDate, Entry>
            },
            type.habitGroupId,
            type.userId

        )
    }

    override fun mapToHabitEntityFromHabitModel(type: HabitModel): HabitEntity {
        return HabitEntity(
            type.localId!!,
            type.serverId!!,
            type.title,
            type.description,
            type.reminderQuestion,
            localDateConverter(type.startDate.toString()),
            localDateConverter(type.endDate.toString()),
            type.isReminderOn,
            localDateTimeConverter(type.reminderTime.toString()),
            type.entries?.map {
                it?.let {
                    entryMapper.mapFromEntryModel(it)
                }
            }?.associateBy { it?.timestamp!! } as? HashMap<LocalDate,EntryEntity>,
            habitGroupId = type.habitGroupId,
            userId = type.userId


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

    override fun mapHabitModelToFromHabitEntity(type: HabitEntity?): HabitModel {
        type!!
        return HabitModel(
            serverId = type.serverId,
            title = type.title,
            description = type.description,
            reminderQuestion = type.reminderQuestion,
            isReminderOn = type.isReminderOn,
            endDate = type.endDate.toString(),
            startDate = type.startDate.toString(),
            reminderTime = type.reminderTime.toString(),
            entries = type.entryList?.values?.map {
                entryMapper.mapToEntryModel(it)
            },
            localId = type.id
        )
    }

}
