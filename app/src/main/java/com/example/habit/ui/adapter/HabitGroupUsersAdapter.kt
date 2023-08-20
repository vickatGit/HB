package com.example.habit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habit.databinding.UserProgressItemBinding
import com.example.habit.ui.callback.HabitClick
import com.example.habit.ui.model.HabitView
import com.example.habit.ui.model.UtilModels.UserGroupThumbProgressModel
import java.text.DecimalFormat
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class HabitGroupUsersAdapter(val habits: MutableList<UserGroupThumbProgressModel>, val habitClick: HabitClick) : RecyclerView.Adapter<HabitGroupUsersAdapter.UserProgressHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProgressHolder {
        return UserProgressHolder(
            UserProgressItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return  habits.size
    }

    override fun onBindViewHolder(holder: UserProgressHolder, position: Int) {
        val habit= habits[holder.absoluteAdapterPosition]
        holder.binding?.let {
            initialiseProgress(habit.habit,it)
            it.userName.text = habit.member.username
            it.userName.setOnClickListener {
                habitClick.habitClick(habitId = position.toString())
            }
        }

    }
    private fun initialiseProgress(habit: HabitView, binding: UserProgressItemBinding?) {
        val totalHabitDuration = ChronoUnit.DAYS.between(habit.startDate, habit.endDate)
        var daysCompleted = 0
        habit.entries?.mapValues {
//            if (it.key.isBefore(LocalDate.now()) && it.key.isEqual(LocalDate.now())) {
            if (it.value.completed) ++daysCompleted
//            }
        }
        val progress = (totalHabitDuration!! / 100f) * daysCompleted!!
        binding?.progress?.progress = progress.roundToInt()
        binding?.progressPercentage?.text = "${DecimalFormat("#.#").format(progress)}%"
    }

    inner class UserProgressHolder(var binding: UserProgressItemBinding?): RecyclerView.ViewHolder(binding!!.root)


}