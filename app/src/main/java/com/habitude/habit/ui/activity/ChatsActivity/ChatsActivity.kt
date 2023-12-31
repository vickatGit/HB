package com.habitude.habit.ui.activity.ChatsActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.habitude.habit.R
import com.habitude.habit.data.local.Pref.AuthPref
import com.habitude.habit.databinding.ActivityChatsBinding
import com.habitude.habit.ui.activity.Chat.ChatActivity
import com.habitude.habit.ui.adapter.RoomsAdapter
import com.habitude.habit.ui.viewmodel.ChatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatsActivity : AppCompatActivity() {
    private lateinit var roomsAdapter: RoomsAdapter
    private var _binding: ActivityChatsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authPref: AuthPref

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
                            roomsAdapter = RoomsAdapter(it.chatRooms,this@ChatsActivity){room_id,roomName,roomType,members,friendImageUrl,adminImageUrl ->
                                val intent = Intent(this@ChatsActivity,ChatActivity::class.java).apply {
                                    putExtra(ChatActivity.ROOM_ID,room_id)
                                    putExtra(ChatActivity.FRIEND_NAME,roomName)
                                    if(members.size==2){
                                        members.forEach {
                                            if(authPref.getUserId()!=it){
                                                putExtra(ChatActivity.FRIEND_ID,it)
                                            }
                                        }
                                    }
                                    putExtra(ChatActivity.FRIEND_IMAGE_URL,friendImageUrl)
                                    putExtra(ChatActivity.ADMIN_IMAGE_URL,adminImageUrl)
                                    putExtra(ChatActivity.IS_ROOM_PRIVATE, roomType=="Personal")
                                }
                                startActivity(intent)
                            }
                            binding.usersRecycler.adapter = roomsAdapter
                            binding.progress.isVisible = false
                        }
                        is ChatsUIState.Error -> {
                            binding.progress.isVisible = false
                        }
                        ChatsUIState.Loading -> {
                            binding.progress.isVisible = true
                        }
                        ChatsUIState.Nothing -> {

                        }

                        is ChatsUIState.Chats ->{ }
                    }
                }
            }
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }
        viewModel.getChatRooms()
    }
}