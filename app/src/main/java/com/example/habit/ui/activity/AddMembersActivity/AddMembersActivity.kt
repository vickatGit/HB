package com.example.habit.ui.activity.AddMembersActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit.R
import com.example.habit.databinding.ActivityAddMembersBinding
import com.example.habit.ui.activity.UserActivity.UserActivity
import com.example.habit.ui.activity.UserSearchActivity.UserSearchUiState
import com.example.habit.ui.adapter.MembersAdapter
import com.example.habit.ui.adapter.UserListAdapter
import com.example.habit.ui.callback.MemberCheckChange
import com.example.habit.ui.callback.OnUserClick
import com.example.habit.ui.model.User.UserView
import com.example.habit.ui.viewmodel.AddMembersViewModel
import com.example.habit.ui.viewmodel.UserSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddMembersActivity : AppCompatActivity() {
    private var _binding: ActivityAddMembersBinding?=null
    private val binding get() = _binding!!

    private var users = mutableListOf<UserView>()
    private var selectedMembers = mutableMapOf<String,String>()
    private val viewModel: AddMembersViewModel by viewModels()
    private lateinit var usersAdapter: MembersAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.usersRecycler.layoutManager = LinearLayoutManager(this@AddMembersActivity)
        usersAdapter = MembersAdapter(users, object : MemberCheckChange {
            override fun onCheckChanged(userId: String, isAdded: Boolean)  {
                if(isAdded) selectedMembers[userId] = userId else selectedMembers.remove(userId)
            }

        })
        binding.usersRecycler.adapter = usersAdapter
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is AddMemberUiState.Success -> {
                            users.clear()
                            users.addAll(it.users)
                            usersAdapter.notifyDataSetChanged()
                            binding.progress.isVisible=false
                        }
                        is AddMemberUiState.Error -> {
                            Toast.makeText(this@AddMembersActivity, it.error, Toast.LENGTH_SHORT).show()
                            binding.progress.isVisible=false
                        }
                        AddMemberUiState.Loading -> {
                            binding.progress.isVisible=true
                        }

                        AddMemberUiState.Nothing -> { binding.progress.isVisible=false }
                    }
                }
            }
        }
        viewModel.getMembers()
    }

    companion object {
        val HABIT_GROUP_ID: String? = "group_habit_id"
    }
}