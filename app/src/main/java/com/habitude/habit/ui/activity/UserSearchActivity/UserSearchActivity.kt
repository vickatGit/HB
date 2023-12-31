package com.habitude.habit.ui.activity.UserSearchActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.habitude.habit.databinding.ActivityUserSearchBinding
import com.habitude.habit.ui.activity.UserActivity.UserActivity
import com.habitude.habit.ui.adapter.UserListAdapter
import com.habitude.habit.ui.adapter.UserSearchAdapter
import com.habitude.habit.ui.callback.OnUserClick
import com.habitude.habit.ui.model.User.UserView
import com.habitude.habit.ui.viewmodel.UserSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserSearchActivity : AppCompatActivity() {

    private var _binding: ActivityUserSearchBinding? = null
    private val binding get() = _binding!!

    private var users = mutableListOf<UserView>()
    private val viewModel: UserSearchViewModel by viewModels()
    private lateinit var usersAdapter: UserSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.usersRecycler.layoutManager = LinearLayoutManager(this@UserSearchActivity)
        usersAdapter = UserSearchAdapter(users, this@UserSearchActivity,object : OnUserClick {
            override fun onUserClick(userId: String) {
                val userIntent = Intent(this@UserSearchActivity,UserActivity::class.java)
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
                            Toast.makeText(this@UserSearchActivity, it.error, Toast.LENGTH_SHORT)
                                .show()
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
        binding.query.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length != 0)
                    lifecycleScope.launch {
                        delay(300)
                        viewModel.getUsersByUserName(binding.query.text.toString())
                    }
                else {
                    users.clear()
                    usersAdapter.notifyDataSetChanged()
                }


            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.back.setOnClickListener {
            onBackPressed()
        }

    }
}