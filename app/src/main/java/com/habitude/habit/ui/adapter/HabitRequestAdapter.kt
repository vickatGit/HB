package com.habitude.habit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.habitude.habit.databinding.HabitRequestNotificationItemLayoutBinding
import com.habitude.habit.domain.models.notification.HabitRequest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HabitRequestAdapter(
    private val habitRequests: List<HabitRequest>,
    private val acceptRequest : (groupHabitId:String) -> Unit,
    private val rejectRequest : (groupHabitId:String) -> Unit,
) : RecyclerView.Adapter<HabitRequestAdapter.HabitRequestHolder>() {

    inner class HabitRequestHolder(val binding: HabitRequestNotificationItemLayoutBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitRequestHolder {
        return HabitRequestHolder(
            HabitRequestNotificationItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return habitRequests.size
    }

    override fun onBindViewHolder(holder: HabitRequestHolder, position: Int) {
        val habit= habitRequests[holder.absoluteAdapterPosition]
        holder.binding.habitTitle.text = habit.habitTitle
        holder.binding.duration.text = "${dateFormatter(habit.startDate)} - ${dateFormatter(habit.endDate)}"
        holder.binding.from.text = "From ${habit.from.username}"
        holder.binding.accept.setOnClickListener {
            acceptRequest(habit.groupHabitId)
        }
        holder.binding.reject.setOnClickListener {
            rejectRequest(habit.groupHabitId)
        }
    }
    private fun dateFormatter(localDate: LocalDate):String{
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
        return localDate.format(formatter)
    }
}