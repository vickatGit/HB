package com.example.habit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.habit.databinding.UserThumbItemLayoutBinding
import com.example.habit.ui.callback.OnUserClick
import com.example.habit.ui.model.User.UserView

class UserListAdapter(private val users: MutableList<UserView>, private val onUserClick: OnUserClick) : RecyclerView.Adapter<UserListAdapter.UserHolder>() {


    inner class UserHolder(val binding: UserThumbItemLayoutBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        return UserHolder(UserThumbItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val binding = holder.binding
        val user = users[holder.absoluteAdapterPosition]
        binding.userName.text = user.username
        binding.root.setOnClickListener {
            onUserClick.onUserClick(user.id!!)
        }
    }
}