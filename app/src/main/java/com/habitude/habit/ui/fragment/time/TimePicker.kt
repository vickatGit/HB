package com.habitude.habit.ui.fragment.time

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.NumberPicker
import com.habitude.habit.R
import com.habitude.habit.databinding.TimeLayoutBinding

import java.util.Calendar
import java.util.Locale

class TimePicker(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private lateinit var selectedTime: Calendar

    private lateinit var hour: NumberPicker
    private lateinit var minute: NumberPicker
    private lateinit var seconds: NumberPicker
    private lateinit var amPm: NumberPicker

    init { initControl(context) }

    private fun initControl(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.time_layout,this)
        hour = findViewById(R.id.hour)
        minute = findViewById(R.id.minute)
        seconds = findViewById(R.id.second)
        amPm = findViewById(R.id.am_pm)
        selectedTime = Calendar.getInstance()
        hour.minValue = 1
        hour.maxValue = 12
        hour.value = selectedTime.get(Calendar.HOUR_OF_DAY)
        minute.minValue = 0
        minute.maxValue = 59
        minute.value = selectedTime.get(Calendar.MINUTE)
        seconds.minValue = 0
        seconds.maxValue = 59
        seconds.value = selectedTime.get(Calendar.SECOND)
        amPm.minValue = 0
        amPm.maxValue = 1
        amPm.displayedValues = arrayOf("AM", "PM")
        amPm.value = selectedTime.get(Calendar.AM_PM)
        val numberFormatter =
            NumberPicker.Formatter { value -> String.format("%02d", value) }
        hour.setFormatter(numberFormatter)
        minute.setFormatter(numberFormatter)
        seconds.setFormatter(numberFormatter)
        hour.setOnValueChangedListener { _, _, newVal ->
            Log.e("TAG", "onValueChange: binding.hour $newVal")
            selectedTime.set(Calendar.HOUR_OF_DAY, newVal)
            Log.e("TAG", "getSelectedTime: " + selectedTime!!.time.toString())
        }
        minute.setOnValueChangedListener { _, _, newVal ->
            Log.e("TAG", "onValueChange: binding.minute $newVal")
            selectedTime.set(Calendar.MINUTE, newVal)
            Log.e("TAG", "getSelectedTime: " + selectedTime!!.time.toString())
        }
        seconds.setOnValueChangedListener { _, _, newVal ->
            Log.e("TAG", "onValueChange: binding.second $newVal")
            selectedTime.set(Calendar.SECOND, newVal)
            Log.e("TAG", "getSelectedTime: " + selectedTime!!.time.toString())
        }
        amPm.setOnValueChangedListener { picker, oldVal, newVal ->
            Log.e("TAG", "onValueChange: binding.hour $newVal")
            if (newVal == 0) selectedTime.set(
                Calendar.AM_PM,
                Calendar.AM
            ) else selectedTime.set(Calendar.AM_PM, Calendar.PM)
        }
    }

    fun getSelectedTime(): Calendar? {
        Log.e("TAG", "getSelectedTime: " + selectedTime!!.time.toString())
        return selectedTime
    }
}