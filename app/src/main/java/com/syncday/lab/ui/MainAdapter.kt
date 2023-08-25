package com.syncday.lab.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.syncday.lab.HomeBean
import com.syncday.lab.R
import com.syncday.lab.databinding.ItemMainBinding
import com.syncday.lab.view.RoundTransform

class MainAdapter(diffCallback: DiffUtil.ItemCallback<HomeBean> = object : DiffUtil.ItemCallback<HomeBean>() {
    override fun areItemsTheSame(oldItem: HomeBean, newItem: HomeBean): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(oldItem: HomeBean, newItem: HomeBean): Boolean {
        return true
    }

}) : ListAdapter<HomeBean, MainAdapter.VH>(diffCallback) {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemMainBinding.bind(itemView)
        fun bind(data: HomeBean){
            binding.title.text = data.title
            binding.subTitle.text = data.author
            Glide.with(itemView)
                .load(data.resId)
                .transform(CenterCrop(), RoundTransform(90f,90f,0f,0f))
                .into(binding.cover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_main,parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

}