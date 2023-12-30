package com.habitude.habit.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.habitude.habit.R
import com.habitude.habit.databinding.UserThumbItemLayoutBinding
import com.habitude.habit.ui.callback.OnUserClick
import com.habitude.habit.ui.model.User.UserView

class UserSearchAdapter(private val users: MutableList<UserView>,val context:Context, private val onUserClick: OnUserClick) : RecyclerView.Adapter<UserSearchAdapter.UserHolder>() {


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
        Glide.with(context).load(user.avatarUrl)
            .placeholder(R.drawable.user)
            .error(Glide.with(context).load(R.drawable.user))
            .into(holder.binding.userImage)
    }
}