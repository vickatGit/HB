package com.example.habit.ui.activity.UserActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.habit.R
import com.example.habit.databinding.ActivityUserBinding
import com.example.habit.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {
    private var friendId: String? = null
    private var _binding: ActivityUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel:UserViewModel  by viewModels()

    companion object {
        const val USER_ID: String = "user_id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        friendId = intent.getStringExtra(USER_ID)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collectLatest {
                    when(it){
                        is UserActivityUiState.Error -> {

                        }
                        UserActivityUiState.Loading -> {

                        }
                        UserActivityUiState.Nothing -> {

                        }
                        is UserActivityUiState.UserFollowStatus -> {
                            binding.followStatus.text = if(it.isFollows) "Following" else "Follow"
                            binding.followStatus.isChecked = it.isFollows
                        }
                    }
                }
            }
        }

        friendId?.let { viewModel.isUserFollowing(it) }

    }


}