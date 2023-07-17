package com.example.habit.ui.mapper

import com.example.habit.domain.models.Entry
import com.example.habit.ui.model.EntryView

class EntryMapper :EntryMapperI<EntryView,Entry> {
    override fun mapToEntry(type: EntryView): Entry {
        return Entry(type.timestamp,type.score)
    }

    override fun mapFromEntry(type: Entry): EntryView {
        return EntryView(type.timestamp,type.score)
    }
}