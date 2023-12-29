package com.habitude.habit.ui.activity.UserActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.habitude.habit.R
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.databinding.ActivityUserBinding
import com.habitude.habit.ui.activity.Chat.ChatActivity
import com.habitude.habit.ui.util.DebounceCheckedChangeListener.OnDebouncCheckedChangeListener
import com.habitude.habit.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {
    private var shouldCallFollowApi: Boolean = false
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
                        is UserActivityUiState.Success -> {
                            Toast.makeText(this@UserActivity,it.msg,Toast.LENGTH_SHORT).show()
                            hideProgress()
                            friendId?.let { it1 -> viewModel.isUserFollowing(it1) }
                        }
                        is UserActivityUiState.Error -> {
                            Toast.makeText(this@UserActivity,it.error,Toast.LENGTH_SHORT).show()
                            hideProgress()
                            binding.followStatus.isChecked=viewModel.followStatus
                        }
                        UserActivityUiState.Loading -> {
                            showProgress()
                        }
                        is UserActivityUiState.UserFollowStatus -> {
                            binding.followStatus.text = if(it.isFollows) "Following" else "Follow"
                            shouldCallFollowApi = false
                            viewModel.followStatus=it.isFollows
                            binding.followStatus.isChecked = it.isFollows
                            shouldCallFollowApi = true
                            hideProgress()
                        }
                        UserActivityUiState.Nothing -> {}
                        is UserActivityUiState.Profile -> {
                            viewModel.user = it.user
                            bindProfileData()
                            hideProgress()
                        }
                    }
                }
            }
        }


        friendId?.let {friendId ->
            viewModel.isUserFollowing(friendId)

        }
        friendId?.let { viewModel.getProfile(it) }

        binding.followStatus.setOnCheckedChangeListener { btnView, isChecked ->
            binding.followStatus.text = if(isChecked) "Following" else "Follow"
            if(isChecked) {
                if(shouldCallFollowApi) viewModel.followUser(friendId!!)
                binding.followStatus.setChipBackgroundColorResource(R.color.white)
                binding.followStatus.setTextColor(resources.getColor(R.color.text_color))
            }
            else {
                if(shouldCallFollowApi) binding.followStatus.setChipBackgroundColorResource(R.color.orange)
                binding.followStatus.setTextColor(resources.getColor(R.color.white))
                viewModel.unfollowUser(friendId!!)
            }
        }
        binding.message.setOnClickListener {
            startActivity(Intent(this,ChatActivity::class.java).apply {
                putExtra(ChatActivity.FRIEND_ID,friendId)
                putExtra(ChatActivity.FRIEND_NAME,viewModel.user?.username)
                putExtra(ChatActivity.IS_ROOM_PRIVATE,true)
            })
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }

    }

    private fun bindProfileData() {
        viewModel.user?.let {
            binding.userBio.text=it.userBio
            binding.userName.text=it.username
            Glide.with(this@UserActivity).load(it.avatarUrl)
                .placeholder(R.drawable.user)
                .error(Glide.with(this@UserActivity).load(R.drawable.user))
                .into(binding.userProfileImage)
        }
    }

    private fun showProgress(){
        binding.progress.isVisible=true
    }
    private fun hideProgress(){
        binding.progress.isVisible=false
    }

}