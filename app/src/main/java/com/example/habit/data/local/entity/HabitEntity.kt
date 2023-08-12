package com.example.habit.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.habit.data.util.HabitRecordSyncType
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
@Entity
data class HabitEntity(
    @PrimaryKey
    var id:String,
    var serverId:String?=null,
    var title:String?,
    var description:String?,
    var reminderQuestion:String?,
    var startDate:LocalDate?,
    var endDate: LocalDate?,
    var isReminderOn : Boolean?,
    var reminderTime:LocalDateTime?,
    var entryList: Map<LocalDate, EntryEntity>? = null,
    var habitSyncType:HabitRecordSyncType=HabitRecordSyncType.SyncedHabit
) : Parcelable{
    constructor():this("",null,null,null,null,null,null,null,null)
}
