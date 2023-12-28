package com.habitude.habit.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.habitude.habit.data.util.HabitGroupRecordSyncType
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Parcelize
data class GroupHabitsEntity(
    @PrimaryKey
    var localId:String,
    var serverId:String?=null,
    var title:String?,
    var description:String?,
    var reminderQuestion:String?,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
    var isReminderOn: Boolean?,
    var reminderTime: LocalDateTime?,
    var members: String?,
    var habitSyncType: HabitGroupRecordSyncType = HabitGroupRecordSyncType.SyncedHabit,
    var admin: String? = null
) : Parcelable