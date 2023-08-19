package com.example.habit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habit.data.local.entity.HabitEntity
import com.example.habit.databinding.GroupHabitThumbBinding
import com.example.habit.databinding.UserProgressItemBinding
import com.example.habit.ui.callback.HabitClick
import java.text.DecimalFormat
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class HabitGroupUsersAdapter(val habits: List<HabitEntity>, val habitClick: HabitClick) : RecyclerView.Adapter<HabitGroupUsersAdapter.UserProgressHolder>() {


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
            initialiseProgress(habit,it)
            it.userName.text = habit.title
            it.userName.setOnClickListener {
                habitClick.habitClick(habitId = position.toString())
            }
        }

    }
    private fun initialiseProgress(habit: HabitEntity, binding: UserProgressItemBinding?) {
        val totalHabitDuration = ChronoUnit.DAYS.between(habit.startDate, habit.endDate)
        var daysCompleted = 0
        habit.entryList?.mapValues {
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