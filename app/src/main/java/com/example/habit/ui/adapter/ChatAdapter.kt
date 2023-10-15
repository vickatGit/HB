package com.example.habit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.habit.data.network.model.ChatModel.MessageModel
import com.example.habit.databinding.TheirChatItemBinding
import com.example.habit.databinding.UrChatItemBinding
import com.example.habit.ui.util.DpPxUtils

class ChatAdapter(
    val userId:String,
    val chats: MutableList<MessageModel>
) : RecyclerView.Adapter<ViewHolder>() {

    private var prevChatHolder: ViewHolder? = null

    inner class UrChatHolder(val binding : UrChatItemBinding) : ViewHolder(binding.root)
    inner class TheirChatHolder(val binding : TheirChatItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if(viewType==0)
            UrChatHolder(UrChatItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        else
            TheirChatHolder(TheirChatItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(holder is UrChatHolder) {
            if(prevChatHolder!=null && prevChatHolder !is UrChatHolder) {
                holder.binding.root.layoutParams.apply {
                    this as RecyclerView.LayoutParams
                    setMargins(
                        DpPxUtils.dpToPX(80f, holder.binding.root.context),
                        DpPxUtils.dpToPX(20f, holder.binding.root.context),
                        DpPxUtils.dpToPX(20f, holder.binding.root.context),
                        DpPxUtils.dpToPX(0f, holder.binding.root.context),
                    )
                }
                holder.binding.root.requestLayout()
            }
            prevChatHolder = holder
            holder.binding.chat.text = chats[holder.absoluteAdapterPosition].msg
        }
        else if(holder is TheirChatHolder) {
            if(prevChatHolder!=null && prevChatHolder !is TheirChatHolder) {
                holder.binding.root.layoutParams.apply {
                    this as RecyclerView.LayoutParams
                    setMargins(
                        DpPxUtils.dpToPX(20f, holder.binding.root.context),
                        DpPxUtils.dpToPX(20f, holder.binding.root.context),
                        DpPxUtils.dpToPX(80f, holder.binding.root.context),
                        DpPxUtils.dpToPX(0f, holder.binding.root.context),
                    )
                }
                holder.binding.root.requestLayout()
            }
            prevChatHolder = holder
            holder.binding.chat.text = chats[holder.absoluteAdapterPosition].msg
        }

    }

    override fun getItemViewType(position: Int): Int {
        return  if(chats.get(position).from==userId) 0 else 1
    }
}