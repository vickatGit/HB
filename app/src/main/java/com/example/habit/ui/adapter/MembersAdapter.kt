package com.example.habit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.habit.databinding.MemberItemLayoutBinding
import com.example.habit.databinding.UserThumbItemLayoutBinding
import com.example.habit.ui.callback.MemberCheckChange
import com.example.habit.ui.callback.OnUserClick
import com.example.habit.ui.model.User.UserView

class MembersAdapter(
    private val originalUsers: MutableList<UserView>,
    private val onMemberCheckChange: MemberCheckChange
) : RecyclerView.Adapter<MembersAdapter.MemberHolder>(), Filterable {

    private var users: List<UserView> = originalUsers

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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val query = constraint?.toString()?.toLowerCase()

                if (query.isNullOrBlank()) {
                    filterResults.values = originalUsers
                } else {
                    val filteredUsers = originalUsers.filter { user ->
                        user.username?.toLowerCase()?.contains(query) == true
                    }
                    filterResults.values = filteredUsers
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                users = results?.values as List<UserView>
                notifyDataSetChanged()
            }
        }
    }
}