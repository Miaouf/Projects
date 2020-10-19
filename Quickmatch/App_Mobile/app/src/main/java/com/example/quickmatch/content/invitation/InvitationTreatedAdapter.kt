package com.example.quickmatch.content.invitation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quickmatch.databinding.ListInvitationTreatedItemBinding
import com.example.quickmatch.network.PlayerMeetObject

class InvitationTreatedAdapter(val clickListener: InvitationTreatedClickListener)
    : ListAdapter<PlayerMeetObject, InvitationTreatedAdapter.ViewHolder>(InvitationTreateatedDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class ViewHolder private constructor(val binding: ListInvitationTreatedItemBinding): RecyclerView.ViewHolder(binding.root) {

        /* bind view values to the datas */
        fun bind(item: PlayerMeetObject, clickListener: InvitationTreatedClickListener) {
            binding.invitation = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        /* inflate layout here to encapsulate, onCreateViewHolder has nothing to know about inflation */
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListInvitationTreatedItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}

class InvitationTreateatedDiffCallback : DiffUtil.ItemCallback<PlayerMeetObject>() {
    override fun areItemsTheSame(oldItem: PlayerMeetObject, newItem: PlayerMeetObject): Boolean {
        return oldItem.invitationId == newItem.invitationId
    }

    override fun areContentsTheSame(oldItem: PlayerMeetObject, newItem: PlayerMeetObject): Boolean {
        return oldItem == newItem
    }


}
class InvitationTreatedClickListener(val clickListener: (invitationId: Int?) -> Unit) {
    fun onClick(invitation: PlayerMeetObject) {
        return clickListener(invitation.invitationId)
    }
}