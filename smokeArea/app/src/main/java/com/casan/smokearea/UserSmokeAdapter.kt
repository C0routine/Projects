package com.casan.smokearea

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.casan.smokearea.databinding.UserSmokeItemBinding

class UserSmokeAdapter:ListAdapter<UserTimeTable, UserSmokeAdapter.ViewHolder>(DiffUtil) {

    inner class ViewHolder(private val binding:UserSmokeItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(user:UserTimeTable){
            binding.userSmokeItem.text = user.time
            binding.userSmokeMoney.text = user.money.toString() +"원"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(UserSmokeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}

object DiffUtil:DiffUtil.ItemCallback<UserTimeTable>(){
    override fun areItemsTheSame(oldItem: UserTimeTable, newItem: UserTimeTable): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserTimeTable, newItem: UserTimeTable): Boolean {
        return oldItem.id == newItem.id
    }
}