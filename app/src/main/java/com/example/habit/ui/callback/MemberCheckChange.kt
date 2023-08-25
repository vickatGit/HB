package com.example.habit.ui.callback

interface MemberCheckChange {
    fun onCheckChanged(userId:String,isAdded:Boolean)
}