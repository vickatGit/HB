package com.example.habit.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.habit.data.local.TypeConverters.EntryMapConverter
import com.example.habit.data.local.TypeConverters.LocalDateConverter
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.HashMap

@Parcelize
@Entity
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null,
    var title:String?,
    var description:String?,
    var reminderQuestion:String?,
    var startDate:LocalDate?,
    var endDate: LocalDate?,
    var isReminderOn : Boolean?,
    var reminderTime:LocalDateTime?,
    var entryList: Map<LocalDate,EntryEntity>? = null
) : Parcelable{
    constructor():this(null,null,null,null,null,null,null,null,null)
}
