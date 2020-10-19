package com.example.quickmatch.content.club

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quickmatch.databinding.ListClubsItemBinding
import com.example.quickmatch.network.ClubAndPlayerBelongClubObject

class ClubAdapter(val clickListener: ClubClickListener) : ListAdapter<ClubAndPlayerBelongClubObject, ClubAdapter.ViewHolder>(ClubDiffCallback()) {

    /* how data should be binded with the viewholder */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    /* how to create a new viewholder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /* custom view holder based on the custom item layout */
    class ViewHolder private constructor(val binding: ListClubsItemBinding) : RecyclerView.ViewHolder(binding.root) {

        /* bind view values to the datas */
        fun bind(item: ClubAndPlayerBelongClubObject, clickListener: ClubClickListener) {
            binding.clubAndPlayer = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        /* inflate layout here to encapsulate, onCreateViewHolder has nothing to know about inflation */
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListClubsItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

/* class used to improve data refresh in the recycler view */
class ClubDiffCallback : DiffUtil.ItemCallback<ClubAndPlayerBelongClubObject>() {

    /* check if items are the same item */
    override fun areItemsTheSame(oldItem: ClubAndPlayerBelongClubObject, newItem: ClubAndPlayerBelongClubObject): Boolean {
        return oldItem.id == newItem.id
    }

    /* check equality between items */
    override fun areContentsTheSame(oldItem: ClubAndPlayerBelongClubObject, newItem: ClubAndPlayerBelongClubObject): Boolean {
        return oldItem == newItem
    }
}

/* Click listener for recycler view items */
class ClubClickListener(val clickListener : (clubId: Int?) -> Unit) {
    fun onClick(club: ClubAndPlayerBelongClubObject) = clickListener(club.id)
}