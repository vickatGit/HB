package com.example.habit.ui.activity.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit.data.local.Pref.AuthPref
import com.example.habit.data.network.model.ChatModel.ChatModel
import com.example.habit.databinding.ActivityChatBinding
import com.example.habit.ui.activity.ChatsActivity.ChatsUIState
import com.example.habit.ui.adapter.ChatAdapter
import com.example.habit.ui.viewmodel.ChatsViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Ack
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    private var userName: String? = null
    private var isRoomPrivate: Boolean=true
    private var roomName: String? =null
    private lateinit var chatController: ChatAdapter
    val chats = mutableListOf<ChatModel>()
    private var roomId: String? = null
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!
    private var friendId:String? = null
    private var userId:String? = null

    private val viewModel: ChatsViewModel by viewModels()
    
    @Inject
    lateinit var socket: Socket

    @Inject
    lateinit var authPref: AuthPref


    override fun onStart() {
        super.onStart()
        socket.emit("ONLINE_STATUS",true,roomId)
    }

    override fun onStop() {
        super.onStop()
        socket.emit("ONLINE_STATUS",false,roomId)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = authPref.getUserId();
        userName = authPref.getUserName()
        friendId = intent.getStringExtra(FRIEND_ID)
        roomName = intent.getStringExtra(FRIEND_NAME)
        roomId = intent.getStringExtra(ROOM_ID)
        isRoomPrivate = intent.getBooleanExtra(IS_ROOM_PRIVATE,true)
        socket.emit("ONLINE_STATUS_REGISTRATION",userId)


        binding.chats.layoutManager = LinearLayoutManager(this)
        chatController = ChatAdapter(userId =userId!!,chats)
        binding.chats.adapter = chatController
        socket.connect()
        if(roomId==null) {
            socket.emit("CREATE_ROOM", Gson().toJson(listOf(friendId,userId)),userId,roomName,Ack{
                Log.e("TAG", "onCreate: ROOM_CREATED ${Gson().toJson(it)}" )
                roomId = it[0].toString()
//                socket.emit("JOIN_ROOM",roomId)
                bindEvents()
            })
        }else{
            bindEvents()
        }



    }

    private fun bindEvents() {
        lifecycleScope.launch { 
            repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.chatsUIState.collectLatest { 
                    when(it){
                        is ChatsUIState.ChatRooms -> {}
                        is ChatsUIState.Chats -> {
                            chats.addAll(it.chats)
                            chatController.notifyDataSetChanged()
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
                    }
                }
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            binding.header.text = roomName
            Log.e("TAG", "bindEvents: roomId ${roomId}", )
        }
        socket.emit("JOIN_ROOM",roomId)
        Log.e("TAG", "bindEvents: online ${isRoomPrivate} ${friendId}", )
        if(isRoomPrivate && friendId!=null){
            socket.on("AM_I_ONLINE"){
                socket.emit("AM_I_ONLINE",roomId,true)
            }
            socket.on("IS_FRIEND_ONLINE",){
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@ChatActivity,"status ${it[0]}",Toast.LENGTH_SHORT).show()
                    if (it[0] as Boolean) {
                        binding.onlineStatus.isVisible = true
                        binding.offlineStatus.isVisible = false
                    } else {
                        binding.offlineStatus.isVisible = true
                        binding.onlineStatus.isVisible = false
                    }
                }
            }
            socket.emit("IS_FRIEND_ONLINE",roomId)

            socket.on("ONLINE_STATUS"){
                CoroutineScope(Dispatchers.Main).launch {
                    if (it[0] as Boolean) {
                        binding.onlineStatus.isVisible = true
                        binding.offlineStatus.isVisible = false
                    } else {
                        binding.offlineStatus.isVisible = true
                        binding.onlineStatus.isVisible = false
                    }
                }
            }
        }
        socket.on("ON_TYPING"){
            CoroutineScope(Dispatchers.Main).launch {
                binding.typer.text = it[0].toString()
            }
        }
        socket.on("STOP_TYPING"){
            CoroutineScope(Dispatchers.Main).launch {
                binding.typer.text = ""
            }
        }
        binding.msgInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                socket.emit("TYPING",roomId,userName)
            }

            override fun afterTextChanged(s: Editable?) {
                lifecycleScope.launch {
                    delay(1000)
                    socket.emit("STOP_TYPING", roomId)
                }
            }

        })

        socket.on("RECEIVE_MESSAGE"){
            Log.e("TAG", "onCreate: ${Gson().toJson(it)}" )
            chats.add(
                ChatModel(
                    null,
                    it.get(1).toString(),
                    null,
                    it.get(0).toString(),
                    "Text",
                    ""
                )
            )
            CoroutineScope(Dispatchers.Main).launch {
                chatController.notifyItemInserted(chats.size-1)
                binding.chats.smoothScrollToPosition(chats.size-1)
            }


        }
        CoroutineScope(Dispatchers.Main).launch {

        binding.send.setOnClickListener {

            val msg = binding.msgInput.text.toString()
            socket.emit(
                "SEND_MESSAGE",
                userId,
                binding.msgInput.text.toString(),
                "Text",
                "",
                roomId
            )
            chats.add(
                ChatModel(
                    null,
                    userId!!,
                    null,
                    binding.msgInput.text.toString(),
                    "Text",
                    ""
                )
            )
            binding.msgInput.text = null
            CoroutineScope(Dispatchers.Main).launch {
                chatController.notifyItemInserted(chats.size-1)
                binding.chats.smoothScrollToPosition(chats.size-1)
            }
        }


        }

        roomId?.let { viewModel.getChats(it) }
    }


    companion object{
        const val IS_ROOM_PRIVATE: String  = "is_room_private"
        const val FRIEND_NAME: String = "friend_name"
        const val ROOM_ID="room_id"
        const val FRIEND_ID="friend_id"
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.off("ON_TYPING")
        socket.off("RECEIVE_MESSAGE")
        socket.off("STOP_TYPING")
        socket.off("ONLINE_STATUS")
        socket.off("IS_FRIEND_ONLINE")
        socket.off("AM_I_ONLINE")

    }
}