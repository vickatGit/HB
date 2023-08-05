package com.example.habit.data.local.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class EntryEntity(
    val timestamp: LocalDate?,
    val score:Int?,
    val completed:Boolean=false
) : Parcelable{
    constructor():this(null,null)
}