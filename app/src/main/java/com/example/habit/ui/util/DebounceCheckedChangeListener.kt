package com.example.habit.ui.util

import android.view.View
import android.widget.CompoundButton

object DebounceCheckedChangeListener{
    fun CompoundButton.OnDebouncCheckedChangeListener(
        duration : Int =1000,
        listener: (btnView:View, isChecked : Boolean ) -> Unit
    ){
        var lastTimeStamp : Long  = 0
        val CustomDebounceListener = CompoundButton.OnCheckedChangeListener { btn, isChecked ->
            val currentTimeMillis = System.currentTimeMillis()
            if(currentTimeMillis - lastTimeStamp > duration){
                listener(btn,isChecked)
            }
        }
        this.setOnCheckedChangeListener(CustomDebounceListener)
    }
}