package com.example.habit.data.Mapper

import com.example.habit.data.local.entity.EntryEntity
import com.example.habit.domain.models.Entry

class EntryMapper : EntryMapperI<EntryEntity,Entry> {
    override fun mapToEntry(type: EntryEntity): Entry {
        return Entry(type.timestamp,type.score,type.completed)
    }

    override fun mapFromEntry(type: Entry): EntryEntity {

        return EntryEntity(type.timestamp,type.score,type.completed)
    }
}