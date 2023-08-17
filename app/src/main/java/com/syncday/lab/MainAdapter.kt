package com.syncday.lab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syncday.lab.databinding.ItemMainBinding

class MainAdapter(diffCallback: DiffUtil.ItemCallback<HomeBean> = object : DiffUtil.ItemCallback<HomeBean>() {
    override fun areItemsTheSame(oldItem: HomeBean, newItem: HomeBean): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(oldItem: HomeBean, newItem: HomeBean): Boolean {
        return true
    }

}) : ListAdapter<HomeBean, MainAdapter.VH>(diffCallback) {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemMainBinding.bind(itemView)
        fun bind(data:HomeBean){
            binding.title.text = data.title
            binding.subTitle.text = data.author
            binding.cover.setImageResource(data.resId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_main,parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

}