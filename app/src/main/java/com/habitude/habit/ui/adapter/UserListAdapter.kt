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

class UserListAdapter(private val users: MutableList<UserView>,val context:Context, private val onUserClick: OnUserClick) : RecyclerView.Adapter<UserListAdapter.UserHolder>(),Filterable {

    private var filteredUsers: MutableList<UserView> = ArrayList(users)



    inner class UserHolder(val binding: UserThumbItemLayoutBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        return UserHolder(UserThumbItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return filteredUsers.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val binding = holder.binding
        val user = filteredUsers[holder.absoluteAdapterPosition]
        binding.userName.text = user.username
        binding.root.setOnClickListener {
            onUserClick.onUserClick(user.id!!)
        }
        Glide.with(context).load(user.avatarUrl)
            .placeholder(R.drawable.user)
            .error(Glide.with(context).load(R.drawable.user))
            .into(holder.binding.userImage)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<UserView>()
                val query = constraint?.toString()?.lowercase()

                if (query.isNullOrEmpty()) {
                    filteredList.addAll(users)
                } else {
                    users.forEach { user ->
                        if (user.username?.lowercase()?.contains(query)==true) {
                            filteredList.add(user)
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredUsers.clear()
                @Suppress("UNCHECKED_CAST")
                results?.values?.let {
                    filteredUsers.addAll(it as List<UserView>)
                }
                notifyDataSetChanged()
            }
        }
    }
}