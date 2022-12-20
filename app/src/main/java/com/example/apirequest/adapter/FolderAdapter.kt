package com.example.apirequest.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.apirequest.databinding.FolderListBinding
import com.example.apirequest.models.FolderData
import java.util.*
import kotlin.collections.ArrayList


class FolderAdapter(private val onItemClicked: (FolderData) -> Unit):
    androidx.recyclerview.widget.ListAdapter<FolderData, FolderAdapter.FolderViewHolder>(DiffCallback){

    private var originalList: List<FolderData> = currentList.toList()
    class FolderViewHolder(private var binding: FolderListBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(folderData: FolderData) {
            binding.apply {
                folderNameTv.text = folderData.name
            }
        }
    }
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<FolderData>() {
            override fun areItemsTheSame(oldItem: FolderData, newItem: FolderData): Boolean {
                return oldItem === newItem
            }
            override fun areContentsTheSame(oldItem: FolderData, newItem: FolderData): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        return FolderViewHolder(
            FolderListBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }
    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener{
            onItemClicked(current)
        }
        holder.bind(current)
    }
    private var unfilteredlist = listOf<FolderData>()
    fun modifyList(list: List<FolderData>){
        unfilteredlist = list
        submitList(list)
    }
    fun filter(query : CharSequence?){
        val list = mutableListOf<FolderData>()
        if(!query.isNullOrEmpty()){
            list.addAll(unfilteredlist.filter {
                it.name.lowercase(Locale.getDefault()).contains(query.toString()
                    .lowercase(Locale.getDefault()))
            })
        }else{
            list.addAll(unfilteredlist)
        }
        submitList(list)
    }

}