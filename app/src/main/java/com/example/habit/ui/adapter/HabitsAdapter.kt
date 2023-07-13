package com.example.habit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habit.databinding.HabitItemLayoutBinding
import com.example.habit.ui.callback.HabitClick
import com.example.habit.ui.model.HabitThumbView

class HabitsAdapter(val habits: MutableList<HabitThumbView>, val habitClick: HabitClick) : RecyclerView.Adapter<HabitsAdapter.HabitHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitHolder {
        return HabitHolder(
            HabitItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return  habits.size
    }

    override fun onBindViewHolder(holder: HabitHolder, position: Int) {
        val habit= habits[holder.absoluteAdapterPosition]
        holder.binding.habit.text = habit.title
        holder.binding.habitContainer.setOnClickListener {
            habitClick.habitClick(habitId = habit.id.toString())
        }
    }

    inner class HabitHolder(val binding: HabitItemLayoutBinding): RecyclerView.ViewHolder(binding.root)
}