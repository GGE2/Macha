package com.example.final_pjt.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.final_pjt.databinding.ItemChatBinding
import com.example.final_pjt.dto.Message
import com.example.final_pjt.dto.MessageTypeEnum
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(var list : List<Message>) : RecyclerView.Adapter<ChatAdapter.ChatHolder>() {
    inner class ChatHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(message: Message){
            val auth = FirebaseAuth.getInstance()
            if(message.user.userToken == auth.currentUser?.uid){
                binding.chatOtherLayout.visibility = View.GONE
                if(message.messageType == MessageTypeEnum.NOTICE){
                    binding.chatMyMsg.gravity = Gravity.CENTER
                }
                binding.chatMyMsg.text = message.text
            } else {
                binding.chatMyLayout.visibility = View.GONE
                binding.chatOtherMsg.text = message.text
                binding.chatOtherNickName.text = message.user.nickname
                if(message.messageType == MessageTypeEnum.NOTICE){
                    binding.chatOtherNickName.visibility = View.GONE
                    binding.chatOtherMsg.gravity = Gravity.CENTER
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        var binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size;
    }
}