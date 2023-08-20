package com.example.habit.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habit.databinding.GroupHabitThumbBinding
import com.example.habit.domain.models.GroupHabitWithHabits
import com.example.habit.domain.models.Habit
import com.example.habit.ui.callback.HabitClick
import java.text.DecimalFormat
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class GroupHabitsAdapter(val habits: List<GroupHabitWithHabits>, val habitClick: HabitClick) : RecyclerView.Adapter<GroupHabitsAdapter.GroupHabitHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHabitHolder {
        return GroupHabitHolder(
            GroupHabitThumbBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return  habits.size
    }

    override fun onBindViewHolder(holder: GroupHabitHolder, position: Int) {
        val habit= habits[holder.absoluteAdapterPosition]
        holder.binding?.let {
            try { initialiseProgress(habit.habits.get(0),it) }catch (e:Exception){ Log.e("tag", "onBindViewHolder: ${e.printStackTrace()}", )}

            it.habit.text = habit.habitGroup.title
            it.totalMembers.text =""+habit.habitGroup.members?.size

            it.habitContainer.setOnClickListener {
                habitClick.habitClick(habitId = habit.habitGroup.id.toString())
            }
        }

    }
    private fun initialiseProgress(habit: Habit, binding: GroupHabitThumbBinding?) {
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

    inner class GroupHabitHolder(var binding: GroupHabitThumbBinding?): RecyclerView.ViewHolder(binding!!.root)


}