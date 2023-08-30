package com.example.habit.ui.activity.AddMembersActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
import com.example.habit.ui.fragment.GroupHabit.GroupFragment
import com.example.habit.ui.model.GroupHabitView
import com.example.habit.ui.model.User.UserView
import com.example.habit.ui.viewmodel.AddMembersViewModel
import com.example.habit.ui.viewmodel.UserSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddMembersActivity : AppCompatActivity() {
    private var _binding: ActivityAddMembersBinding?=null
    private val binding get() = _binding!!

    private var users = mutableListOf<UserView>()
    private val viewModel: AddMembersViewModel by viewModels()
    private lateinit var usersAdapter: MembersAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDefaultContractResult()
        viewModel.groupHabit=intent.getParcelableExtra(HABIT_GROUP)
        binding.usersRecycler.layoutManager = LinearLayoutManager(this@AddMembersActivity)
        usersAdapter = MembersAdapter(users, object : MemberCheckChange {
            override fun onCheckChanged(userId: String, isAdded: Boolean)  {
                if(isAdded) viewModel.selectedMembers[userId] = userId else viewModel.selectedMembers.remove(userId)
                if(viewModel.selectedMembers.isEmpty()){
                    binding.addMembers.setBackgroundColor(resources.getColor(R.color.light_orange))
                    binding.addMembers.isClickable=false
                }else{
                    binding.addMembers.setBackgroundColor(resources.getColor(R.color.orange))
                    binding.addMembers.isClickable=true
                }
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
                            val intent = Intent()
                            intent.putExtra(GroupFragment.IS_MEMBERS_ARE_ADDED,true)
                            setResult(RESULT_OK,intent)

                            onBackPressed()
                        }
                        AddMemberUiState.Loading -> {
                            binding.progress.isVisible=true
                        }
                        AddMemberUiState.Nothing -> { binding.progress.isVisible=false }
                                            }
                }
            }
        }
        binding.addMembers.setOnClickListener {
            viewModel.addMembersToGroupHabit()
        }
        binding.search.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length != 0)
                    lifecycleScope.launch {
                        delay(300)
                        usersAdapter.filter.filter(s.toString().trim())
                    }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

        })
        binding.back.setOnClickListener {
            onBackPressed()
        }
        viewModel.getMembers()
    }

    private fun setDefaultContractResult() {
        val intent = Intent()
        intent.putExtra(GroupFragment.IS_MEMBERS_ARE_ADDED,false)
        setResult(RESULT_OK,intent)
    }

    companion object {
        val HABIT_GROUP: String? = "group_habit"
    }
}