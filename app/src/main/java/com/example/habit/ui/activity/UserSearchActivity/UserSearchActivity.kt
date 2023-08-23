package com.example.habit.ui.activity.UserSearchActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit.data.network.model.UsersModel.User
import com.example.habit.databinding.ActivityUserSearchBinding
import com.example.habit.ui.adapter.UserListAdapter
import com.example.habit.ui.viewmodel.UserSearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration

@AndroidEntryPoint
class UserSearchActivity : AppCompatActivity() {

    private var _binding: ActivityUserSearchBinding? = null
    private val binding get() = _binding!!

    private var users = mutableListOf<User>()
    private val viewModel: UserSearchViewModel by viewModels()
    private lateinit var usersAdapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.usersRecycler.layoutManager = LinearLayoutManager(this@UserSearchActivity)
        usersAdapter = UserListAdapter(users)
        binding.usersRecycler.adapter = usersAdapter
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is UserSearchUiState.Success -> {
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


    }
}