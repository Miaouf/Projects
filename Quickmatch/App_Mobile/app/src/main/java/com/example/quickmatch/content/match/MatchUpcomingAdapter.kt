package com.example.quickmatch.content.match

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quickmatch.databinding.ListMatchUpcomingItemBinding
import com.example.quickmatch.network.PlayerMeetObject

class MatchUpcomingAdapter(val clickListener: MatchUpcomingClickListener) : ListAdapter<PlayerMeetObject, MatchUpcomingAdapter.ViewHolder>(MatchUpcomingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class ViewHolder private constructor(val binding: ListMatchUpcomingItemBinding): RecyclerView.ViewHolder(binding.root) {

        /* bind view values to the datas */
        fun bind(item: PlayerMeetObject, clickListener: MatchUpcomingClickListener) {
            binding.match = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        /* inflate layout here to encapsulate, onCreateViewHolder has nothing to know about inflation */
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListMatchUpcomingItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}

class MatchUpcomingDiffCallback : DiffUtil.ItemCallback<PlayerMeetObject>() {
    override fun areItemsTheSame(oldItem: PlayerMeetObject, newItem: PlayerMeetObject): Boolean {
        return oldItem.meetId == newItem.meetId && oldItem.playerId == newItem.playerId
    }

    override fun areContentsTheSame(oldItem: PlayerMeetObject, newItem: PlayerMeetObject): Boolean {
        return oldItem == newItem
    }


}
class MatchUpcomingClickListener(val clickListener: (matchId: Int?) -> Unit) {
    fun onClick(match: PlayerMeetObject) {
        return clickListener(match.meetId)
    }
}