package com.example.habit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.habit.data.network.model.RoomModel.Room
import com.example.habit.databinding.UserThumbItemLayoutBinding

class RoomsAdapter(
    private val rooms:List<Room>,
    val roomClick:(roomId:String,roomName:String,roomType:String,member:List<String>) -> Unit
) : RecyclerView.Adapter<RoomsAdapter.RoomHolder>() {

    inner class RoomHolder(val binding : UserThumbItemLayoutBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomHolder {
        return RoomHolder(
            UserThumbItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

    override fun onBindViewHolder(holder: RoomHolder, position: Int) {
        val room =rooms[holder.absoluteAdapterPosition]
        holder.binding.userName.text = room.roomName
        holder.binding.root.setOnClickListener {
            roomClick(
                room.id,
                room.roomName,
                room.roomType,
                room.members
            )
        }
    }
}