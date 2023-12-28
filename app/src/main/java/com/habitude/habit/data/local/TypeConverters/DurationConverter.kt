package com.habitude.habit.data.local.TypeConverters

import androidx.room.TypeConverter
import java.time.Duration

class DurationConverter {
    @TypeConverter
    fun fromDuration(value: Duration): Long {
        return value.toMillis()
    }

    @TypeConverter
    fun toDuration(value: Long): Duration {
        return Duration.ofMillis(value)
    }
}