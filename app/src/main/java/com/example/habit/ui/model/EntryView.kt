package com.example.habit.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class EntryView(
    val timestamp: LocalDate?,
    var score:Int?=0,
    var completed:Boolean=false
) : Parcelable