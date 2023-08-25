package com.syncday.lab.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syncday.lab.HomeBean
import com.syncday.lab.R
import com.syncday.lab.databinding.ItemSecondBinding

class SecondAdapter(diffCallback: DiffUtil.ItemCallback<HomeBean> = object : DiffUtil.ItemCallback<HomeBean>() {
    override fun areItemsTheSame(oldItem: HomeBean, newItem: HomeBean): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(oldItem: HomeBean, newItem: HomeBean): Boolean {
        return true
    }

}) : ListAdapter<HomeBean, SecondAdapter.VH>(diffCallback) {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSecondBinding.bind(itemView)
        fun bind(data: HomeBean){
            binding.title.text = data.title
            binding.subTitle.text = data.author
            binding.cover.setImageResource(data.resId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_second,parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

}