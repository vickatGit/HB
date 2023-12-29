package com.habitude.habit.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.habitude.habit.R
import com.habitude.habit.data.network.model.RoomModel.Room
import com.habitude.habit.databinding.UserThumbItemLayoutBinding

class RoomsAdapter(
    private val rooms:List<Room>,
    private val context:Context,
    val roomClick:(roomId:String,roomName:String,roomType:String,member:List<String>,friendImgUrl:String,adminImageUrl:String) -> Unit
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
                room.members,
                room.roomThumbUrl,
                room.adminImageUrl
            )
        }
        Glide.with(context).load(room.roomThumbUrl)
            .placeholder(R.drawable.user)
            .error(Glide.with(context).load(R.drawable.user))
            .into(holder.binding.userImage)
    }
}