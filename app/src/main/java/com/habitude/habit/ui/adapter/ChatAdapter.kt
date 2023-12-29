package com.habitude.habit.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.habitude.habit.R
import com.habitude.habit.data.network.model.ChatModel.ChatModel
import com.habitude.habit.databinding.TheirChatItemBinding
import com.habitude.habit.databinding.UrChatItemBinding
import com.habitude.habit.ui.util.DpPxUtils

class ChatAdapter(
    val userId: String,
    val chats: MutableList<ChatModel>,
    val friendImageUrl: String?,
    val adminImageUrl: String?,
    val context:Context
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
            Log.e("TAG", "onBindViewHolder: admin $adminImageUrl" )
            Glide.with(context).load(adminImageUrl)
                .placeholder(R.drawable.user)
                .error(Glide.with(context).load(R.drawable.user))
                .into(holder.binding.avatar)
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
            Glide.with(context).load(friendImageUrl)
                .placeholder(R.drawable.user)
                .error(Glide.with(context).load(R.drawable.user))
                .into(holder.binding.avatar)
            prevChatHolder = holder
            holder.binding.chat.text = chats[holder.absoluteAdapterPosition].msg
        }

    }

    override fun getItemViewType(position: Int): Int {
        return  if(chats.get(position).from==userId) 0 else 1
    }
}