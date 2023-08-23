package com.example.habit.ui.activity.UserSearchActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit.databinding.ActivityUserSearchBinding
import com.example.habit.ui.adapter.UserListAdapter
import com.example.habit.ui.viewmodel.UserSearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserSearchActivity : AppCompatActivity() {

    private var _binding: ActivityUserSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel :UserSearchViewModel  by viewModels()
    private lateinit var usersAdapter:UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collectLatest {
                    when(it){
                        is UserSearchUiState.Success -> {
                            binding.usersRecycler.layoutManager=LinearLayoutManager(this@UserSearchActivity)
                            usersAdapter = UserListAdapter(it.users)
                            binding.usersRecycler.adapter = usersAdapter
                        }
                        is UserSearchUiState.Error -> {
                            Toast.makeText(this@UserSearchActivity,it.error,Toast.LENGTH_SHORT).show()
                        }
                        UserSearchUiState.Loading -> {

                        }
                        UserSearchUiState.Nothing -> {}
                    }
                }
            }
        }

    }
}