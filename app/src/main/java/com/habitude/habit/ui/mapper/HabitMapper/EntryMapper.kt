package com.habitude.habit.ui.mapper.HabitMapper

import com.habitude.habit.domain.models.Entry
import com.habitude.habit.ui.model.EntryView

class EntryMapper : EntryMapperI<EntryView, Entry> {
    override fun mapToEntry(type: EntryView): Entry {
        return Entry(type.timestamp,type.score,type.completed)
    }

    override fun mapFromEntry(type: Entry): EntryView {
        return EntryView(type.timestamp,type.score,type.completed)
    }
}