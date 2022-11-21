package com.example.final_pjt.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.final_pjt.databinding.ItemUserBinding
import com.example.final_pjt.dto.User

class UserAdapter(var users:List<User>) : RecyclerView.Adapter<UserAdapter.UserHolder>(){
    class UserHolder(var binding:ItemUserBinding):RecyclerView.ViewHolder(binding.root) {

        fun bindData(user:User){
            Glide.with(binding.root).load(user.profileImg).into(binding.itemUserImg)
            binding.itemUserTv.text = user.nickname.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        var binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserHolder(binding)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bindData(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }
    inner class HorizonSpaceItemDecoration(private val horizonSpaceItemDecoration:Int)
        :RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.right = horizonSpaceItemDecoration
        }
        }

}