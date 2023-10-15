package com.example.habit.ui.activity.ChatsActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit.R
import com.example.habit.databinding.ActivityChatsBinding
import com.example.habit.ui.adapter.RoomsAdapter
import com.example.habit.ui.viewmodel.ChatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatsActivity : AppCompatActivity() {
    private lateinit var roomsAdapter: RoomsAdapter
    private var _binding: ActivityChatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel:ChatsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.chatsUIState.collectLatest {
                    when(it){
                        is ChatsUIState.ChatRooms -> {
                            binding.usersRecycler.layoutManager = LinearLayoutManager(this@ChatsActivity)
                            roomsAdapter = RoomsAdapter(it.chatRooms){    }
                            binding.usersRecycler.adapter = roomsAdapter
                        }
                        is ChatsUIState.Error -> {

                        }
                        ChatsUIState.Loading -> {

                        }
                        ChatsUIState.Nothing -> {

                        }
                    }
                }
            }
        }
        viewModel.getChatRooms()
    }
}