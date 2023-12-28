package com.habitude.habit.ui.activity.FollowFollowingActivity

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
import com.habitude.habit.R
import com.habitude.habit.databinding.ActivityFollowFollowingBinding
import com.habitude.habit.databinding.ActivityUserSearchBinding
import com.habitude.habit.ui.activity.UserActivity.UserActivity
import com.habitude.habit.ui.activity.UserSearchActivity.UserSearchUiState
import com.habitude.habit.ui.adapter.UserListAdapter
import com.habitude.habit.ui.callback.OnUserClick
import com.habitude.habit.ui.model.User.UserView
import com.habitude.habit.ui.viewmodel.UserSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FollowFollowingActivity : AppCompatActivity() {
    private var _binding: ActivityFollowFollowingBinding? = null
    private val binding get() = _binding!!


    private var users = mutableListOf<UserView>()
    private val viewModel: UserSearchViewModel by viewModels()
    private lateinit var usersAdapter: UserListAdapter

    private var shouldShowFollowers=false
    companion object{
        const val IS_FOLLOWERS = "should_show_followers"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFollowFollowingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        shouldShowFollowers=intent.getBooleanExtra(IS_FOLLOWERS,false)
        binding.usersRecycler.layoutManager = LinearLayoutManager(this@FollowFollowingActivity)
        usersAdapter = UserListAdapter(users, object : OnUserClick {
            override fun onUserClick(userId: String) {
                val userIntent = Intent(this@FollowFollowingActivity, UserActivity::class.java)
                userIntent.putExtra(UserActivity.USER_ID,userId)
                startActivity(userIntent)

            }

        })
        binding.usersRecycler.adapter = usersAdapter
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is UserSearchUiState.Success -> {
                            users.clear()
                            users.addAll(it.users)
                            usersAdapter.notifyDataSetChanged()
                            binding.progress.isVisible=false
                        }
                        is UserSearchUiState.Error -> {
                            Toast.makeText(this@FollowFollowingActivity, it.error, Toast.LENGTH_SHORT).show()
                            binding.progress.isVisible=false
                        }
                        UserSearchUiState.Loading -> {
                            binding.progress.isVisible=true
                        }

                        UserSearchUiState.Nothing -> { binding.progress.isVisible=false }
                    }
                }
            }
        }
        if(shouldShowFollowers) viewModel.getFollowers() else viewModel.getFollowings()
        binding.back.setOnClickListener {
            onBackPressed()
        }


    }
}