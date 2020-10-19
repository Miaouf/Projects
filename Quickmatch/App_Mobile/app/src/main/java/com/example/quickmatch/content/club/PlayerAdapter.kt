package com.example.quickmatch.content.club

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quickmatch.content.player
import com.example.quickmatch.databinding.ListClubPlayerItemBinding
import com.example.quickmatch.network.PlayerAndPlayerBelongClubObject

class PlayerAdapter(val clickListener: PlayerClickListener) : ListAdapter<PlayerAndPlayerBelongClubObject, PlayerAdapter.ViewHolder>(PlayerDiffCallback()) {

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
    class ViewHolder private constructor(val binding: ListClubPlayerItemBinding) : RecyclerView.ViewHolder(binding.root) {

        /* bind view values to the datas */
        fun bind(item: PlayerAndPlayerBelongClubObject, clickListener: PlayerClickListener) {
            binding.player = item
            binding.clickListener = clickListener
            binding.loggedPlayer = player
            binding.executePendingBindings()
        }

        /* inflate layout here to encapsulate, onCreateViewHolder has nothing to know about inflation */
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListClubPlayerItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

/* class used to improve data refresh in the recycler view */
class PlayerDiffCallback : DiffUtil.ItemCallback<PlayerAndPlayerBelongClubObject>() {

    /* check if items are the same item */
    override fun areItemsTheSame(oldItem: PlayerAndPlayerBelongClubObject, newItem: PlayerAndPlayerBelongClubObject): Boolean {
        return oldItem.id == newItem.id
    }

    /* check equality between items */
    override fun areContentsTheSame(oldItem: PlayerAndPlayerBelongClubObject, newItem: PlayerAndPlayerBelongClubObject): Boolean {
        return oldItem == newItem
    }
}

/* Click listener for recycler view items */
class PlayerClickListener(val clickListener : (playerId: Int?) -> Unit) {
    fun onClick(clickedPlayer: PlayerAndPlayerBelongClubObject) {
        if(!clickedPlayer.isPrivate!!) clickListener(clickedPlayer.id)
    }
}