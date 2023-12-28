package com.habitude.habit.data.local.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class EntryEntity(
    val timestamp: LocalDate?,
    var score:Int?,
    var completed:Boolean=false
) : Parcelable{
    constructor():this(null,null)
}