package com.example.habit.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class Entry(
    val timestamp: LocalDate?,
    var score:Int?,
    val completed:Boolean=false
) : Parcelable
