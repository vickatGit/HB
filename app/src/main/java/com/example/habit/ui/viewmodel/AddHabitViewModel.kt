package com.example.habit.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habit.data.models.Habit
import com.example.habit.domain.UseCases.AddHabitUseCase
import com.example.habit.domain.UseCases.ScheduleAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHabitViewModel @Inject constructor(
    private val addHabitUseCase: AddHabitUseCase,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase
): ViewModel() {

    fun addHabit(habit: Habit,context:Context){
        viewModelScope.launch {
            try {
                val id = addHabitUseCase(habit = habit)
                if(id>0){
                    Log.e("TAG", "addHabit: Insertion Success", )
                    scheduleAlarmUseCase(id.toInt(),"alaram shedulede",habit.reminderTime, context = context)
                }else{
                    Log.e("TAG", "addHabit: Insertion failed", )
                }
            }catch (e:Exception){
                Log.e("TAG", "addHabit: Insertion failed some error occurred", )
            }


        }
    }
}