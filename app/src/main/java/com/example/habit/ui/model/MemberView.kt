package com.example.habit.ui.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MemberView(
    val userId: String? = null,
    var username: String? = null,
) : Parcelable
