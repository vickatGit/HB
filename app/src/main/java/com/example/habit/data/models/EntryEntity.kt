package com.example.habit.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp
import java.time.LocalDate

@Parcelize
data class EntryEntity(
    val timestamp: LocalDate?,
    val score:Int?,
    val completed:Boolean=false
) : Parcelable{
    constructor():this(null,null)
}