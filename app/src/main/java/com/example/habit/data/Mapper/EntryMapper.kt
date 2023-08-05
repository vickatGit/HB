package com.example.habit.data.Mapper

import com.example.habit.data.local.entity.EntryEntity
import com.example.habit.data.network.model.HabitsListModel.EntryModel
import com.example.habit.domain.models.Entry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EntryMapper : EntryMapperI<EntryEntity,Entry,EntryModel> {
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
}