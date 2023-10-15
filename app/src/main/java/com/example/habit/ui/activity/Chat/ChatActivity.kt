package com.example.habit.ui.activity.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habit.data.local.Pref.AuthPref
import com.example.habit.data.network.model.ChatModel.MessageModel
import com.example.habit.databinding.ActivityChatBinding
import com.example.habit.ui.adapter.ChatAdapter
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    private lateinit var chatController: ChatAdapter
    val chats = mutableListOf<MessageModel>()
    private val roomId: String = "65295f72d360bbe3b880c3f2"
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!
    private var friendId:String? = null
    private var userId:String? = null

    @Inject
    lateinit var socket: Socket

    @Inject
    lateinit var authPref: AuthPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        friendId = intent.getStringExtra(FRIEND_ID)
        userId = authPref.getUserId();
        binding.chats.layoutManager = LinearLayoutManager(this)
        chatController = ChatAdapter(userId =userId!!,chats)
        binding.chats.adapter = chatController

        socket.emit("CREATE_ROOM", Gson().toJson(listOf(friendId,userId)),userId,"Test Chat")
        socket.emit("JOIN_ROOM",roomId)

        socket.on("RECEIVE_MESSAGE"){
            Log.e("TAG", "onCreate: ${Gson().toJson(it)}" )
            chats.add(
                MessageModel(
                    it.get(1).toString(),
                    it.get(2).toString(),
                    it.get(0).toString(),
                    "Text",
                    ""
                )
            )
            CoroutineScope(Dispatchers.Main).launch { chatController.notifyItemInserted(chats.size-1) }


        }

        binding.send.setOnClickListener {

            val msg = binding.msgInput.text.toString()
            socket.emit("SEND_MESSAGE",userId,friendId,binding.msgInput.text.toString(),"Text","",roomId)
            chats.add(
                MessageModel(
                    userId!!,
                    friendId!!,
                    binding.msgInput.text.toString(),
                    "Text",
                    ""
                )
            )
            binding.msgInput.text=null
            CoroutineScope(Dispatchers.Main).launch { chatController.notifyItemInserted(chats.size-1) }

        }


    }



    companion object{
        final const val FRIEND_ID="friend_id"
    }
}