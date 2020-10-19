package com.example.quickmatch.content.club

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quickmatch.databinding.ListClubsItemBinding
import com.example.quickmatch.databinding.ListClubsJoinItemBinding
import com.example.quickmatch.network.ClubObject

class ClubJoinAdapter(val clickListener: ClubJoinClickListener) : ListAdapter<ClubObject, ClubJoinAdapter.ViewHolder>(ClubJoinDiffCallback()) {

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
    class ViewHolder private constructor(val binding: ListClubsJoinItemBinding) : RecyclerView.ViewHolder(binding.root) {

        /* bind view values to the datas */
        fun bind(item: ClubObject, clickListener: ClubJoinClickListener) {
            binding.club = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        /* inflate layout here to encapsulate, onCreateViewHolder has nothing to know about inflation */
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListClubsJoinItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

/* class used to improve data refresh in the recycler view */
class ClubJoinDiffCallback : DiffUtil.ItemCallback<ClubObject>() {

    /* check if items are the same item */
    override fun areItemsTheSame(oldItem: ClubObject, newItem: ClubObject): Boolean {
        return oldItem.id == newItem.id
    }

    /* check equality between items */
    override fun areContentsTheSame(oldItem: ClubObject, newItem: ClubObject): Boolean {
        return oldItem == newItem
    }
}

/* Click listener for recycler view items */
class ClubJoinClickListener(val clickListener : (clubId: Int?) -> Unit) {
    fun onClick(club: ClubObject) = clickListener(club.id)
}