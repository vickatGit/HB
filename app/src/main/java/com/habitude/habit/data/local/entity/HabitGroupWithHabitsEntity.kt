package com.habitude.habit.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class HabitGroupWithHabitsEntity(
    @Embedded
    val habitGroup:GroupHabitsEntity,
    @Relation(
        parentColumn = "localId",
        entityColumn = "habitGroupLocalId",
    )
    var habits : List<HabitEntity>

)