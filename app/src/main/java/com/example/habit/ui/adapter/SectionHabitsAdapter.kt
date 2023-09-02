package com.example.habit.ui.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.databinding.UserProgressSectionHabitIemLayoutBinding
import com.example.habit.ui.model.Epoxy.ProgressSectionHabit

class SectionHabitsAdapter(
    val habits: MutableList<ProgressSectionHabit>,
    val progressTypographyProperties: HomeElements.Typography
) : RecyclerView.Adapter<SectionHabitsAdapter.SectionHabitItemHolder>() {

    inner class SectionHabitItemHolder(val binding:UserProgressSectionHabitIemLayoutBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionHabitItemHolder {
        return SectionHabitItemHolder(
            UserProgressSectionHabitIemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return habits.size
    }

    override fun onBindViewHolder(holder: SectionHabitItemHolder, position: Int) {
        val binding = holder.binding
        binding.habitName.text=habits[holder.absoluteAdapterPosition].habitName
        binding.isHabitCompleted.isChecked=habits[holder.absoluteAdapterPosition].isCompleted
        binding.isHabitCompleted.isEnabled=false

        binding.habitName.setTextColor(Color.parseColor(progressTypographyProperties.textColor))
        binding.habitName.textSize=progressTypographyProperties.headerTextSize
        val userTextStyle=
            if(progressTypographyProperties.headerTextStyle.toLowerCase()=="bold") Typeface.BOLD
            else if(progressTypographyProperties.headerTextStyle.toLowerCase()=="normal") Typeface.NORMAL
            else if(progressTypographyProperties.headerTextStyle.toLowerCase()=="italic") Typeface.ITALIC
            else if(progressTypographyProperties.headerTextStyle.toLowerCase()=="bold_italic") Typeface.BOLD_ITALIC
            else Typeface.NORMAL
        binding.habitName.typeface= Typeface.create(binding.habitName.typeface,userTextStyle)
    }
}