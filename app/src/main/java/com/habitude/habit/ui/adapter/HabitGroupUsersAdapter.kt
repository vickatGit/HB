package com.habitude.habit.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.habitude.habit.R
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.databinding.UserProgressItemBinding
import com.habitude.habit.domain.Repository.AuthRepo
import com.habitude.habit.ui.callback.HabitClick
import com.habitude.habit.ui.model.HabitView
import com.habitude.habit.ui.model.UtilModels.UserGroupThumbProgressModel
import java.text.DecimalFormat
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class HabitGroupUsersAdapter(val habits: MutableList<UserGroupThumbProgressModel>, val habitClick: HabitClick) : RecyclerView.Adapter<HabitGroupUsersAdapter.UserProgressHolder>() {

    private var selectedPosition = 0
    private lateinit var recycler :RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler=recyclerView
    }

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

            if(selectedPosition==holder.absoluteAdapterPosition){
                it.userName.setChipBackgroundColorResource(R.color.orange)
                it.userName.setTextColor(it.userName.resources.getColor(R.color.white))
            }else{
                it.userName.setChipBackgroundColorResource(R.color.white)
                it.userName.setTextColor(it.userName.resources.getColor(R.color.text_color))
            }
            it.userName.text = habit.member.username
            it.userName.setOnClickListener {
                recycler.post {
                    val previousItem: Int = selectedPosition
                    selectedPosition= holder.absoluteAdapterPosition

                    habitClick.habitClick(habitId = position.toString())
                    notifyItemChanged(previousItem)
                    notifyItemChanged(selectedPosition)
                }
            }
        }

    }
    private fun initialiseProgress(habit: HabitView, binding: UserProgressItemBinding?) {
        val totalHabitDuration = ChronoUnit.DAYS.between(habit.startDate, habit.endDate)+1
        var daysCompleted = 0
        habit.entries?.mapValues {
            if (it.value.completed) ++daysCompleted
        }
        Log.e("TAG", "initialiseProgress: duration ${totalHabitDuration}", )
        Log.e("TAG", "initialiseProgress: days completed $daysCompleted", )
        Log.e("TAG", "initialiseProgress: ${(daysCompleted / totalHabitDuration) * 100f}-------------", )
        val progress = (daysCompleted.toFloat() / totalHabitDuration.toFloat()) * 100f
        binding?.progress?.progress = progress.toInt()
        binding?.progressPercentage?.text = "${DecimalFormat("#.#").format(progress)}%"
    }

    inner class UserProgressHolder(var binding: UserProgressItemBinding?): RecyclerView.ViewHolder(binding!!.root)


}