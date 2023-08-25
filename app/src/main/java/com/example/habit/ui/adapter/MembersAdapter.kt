package com.example.habit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habit.databinding.MemberItemLayoutBinding
import com.example.habit.databinding.UserThumbItemLayoutBinding
import com.example.habit.ui.callback.MemberCheckChange
import com.example.habit.ui.callback.OnUserClick
import com.example.habit.ui.model.User.UserView

class MembersAdapter(
    private val users: MutableList<UserView>,
    private val onMemberCheckChange: MemberCheckChange
) : RecyclerView.Adapter<MembersAdapter.MemberHolder>() {


    inner class MemberHolder(val binding: MemberItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        return MemberHolder(
            MemberItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        val binding = holder.binding
        val user = users[holder.absoluteAdapterPosition]
        binding.userName.text = user.username
        binding.memberCheck.setOnCheckedChangeListener { _, isChecked ->
                onMemberCheckChange.onCheckChanged(user.id!!, isChecked)
        }
    }
}