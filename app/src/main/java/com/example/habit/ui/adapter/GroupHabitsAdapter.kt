package com.example.habit.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habit.data.local.Pref.AuthPref
import com.example.habit.databinding.GroupHabitThumbBinding
import com.example.habit.domain.models.GroupHabitWithHabits
import com.example.habit.domain.models.Habit
import com.example.habit.ui.callback.HabitClick
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt


class GroupHabitsAdapter(val userId:String, val habits: List<GroupHabitWithHabits>, val habitClick: HabitClick) : RecyclerView.Adapter<GroupHabitsAdapter.GroupHabitHolder>() {


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
        Log.e("TAG", "onBindViewHolder: grouHabit $habit", )
        holder.binding?.let { binding ->
            val userHabit =  habit.habits.find { it.userId.equals(userId) }
            userHabit?.let {  habit ->  initialiseProgress(habit,binding) }

            binding.habit.text = habit.habitGroup.title
            binding.totalMembers.text =""+habit.habitGroup.members?.size

            binding.habitContainer.setOnClickListener {
                habitClick.habitClick(habitId = habit.habitGroup.id.toString())
            }
        }

    }
    private fun initialiseProgress(habit: Habit, binding: GroupHabitThumbBinding?) {
        val totalHabitDuration = ChronoUnit.DAYS.between(habit.startDate, habit.endDate)+1
        var daysCompleted = 0
        habit.entries?.mapValues {
//            if (it.key.isBefore(LocalDate.now()) && it.key.isEqual(LocalDate.now())) {
            if (it.value.completed) ++daysCompleted
//            }
        }
        val progress = (daysCompleted.toFloat() / totalHabitDuration.toFloat()) * 100f
        binding?.progress?.progress = progress.toInt()
        binding?.progressPercentage?.text = "${DecimalFormat("#.#").format(progress)}%"
    }

    inner class GroupHabitHolder(var binding: GroupHabitThumbBinding?): RecyclerView.ViewHolder(binding!!.root)


}