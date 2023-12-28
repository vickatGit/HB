package com.habitude.habit.ui.callback

interface MemberCheckChange {
    fun onCheckChanged(userId:String,isAdded:Boolean)
}