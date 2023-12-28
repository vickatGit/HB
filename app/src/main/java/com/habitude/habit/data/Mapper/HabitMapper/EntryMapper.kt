package com.habitude.habit.data.Mapper.HabitMapper

import com.habitude.habit.data.local.entity.EntryEntity
import com.habitude.habit.data.network.model.HabitsListModel.EntryModel
import com.habitude.habit.domain.models.Entry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EntryMapper : EntryMapperI<EntryEntity, Entry, EntryModel> {
    override fun mapToEntry(type: EntryEntity): Entry {
        return Entry(type.timestamp,type.score,type.completed)
    }

    override fun mapFromEntry(type: Entry): EntryEntity {
        return EntryEntity(type.timestamp,type.score,type.completed)
    }

    override fun mapFromEntryModel(type: EntryModel): EntryEntity {
        return EntryEntity(localDateConverter(type.timestamp!!),type.score,type.completed?:false)
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

    override fun mapToEntryModel(type: EntryEntity): EntryModel {
        return EntryModel(type.score,type.completed,null, timestamp = type.timestamp.toString())
    }
}