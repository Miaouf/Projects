package com.example.quickmatch.content.invitation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quickmatch.databinding.ListInvitationNewItemBinding
import com.example.quickmatch.network.InvitationObject
import com.example.quickmatch.network.PlayerMeetObject

class InvitationNewAdapter(val clickListenerAccept: InvitationNewClickListenerAccept, val clickListenerDecline: InvitationNewClickListenerDecline)
    : ListAdapter<PlayerMeetObject, InvitationNewAdapter.ViewHolder>(InvitationNewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListenerAccept, clickListenerDecline)
    }

    class ViewHolder private constructor(val binding: ListInvitationNewItemBinding): RecyclerView.ViewHolder(binding.root) {

        /* bind view values to the datas */
        fun bind(item: PlayerMeetObject, clickListenerAccept: InvitationNewClickListenerAccept, clickListenerDecline: InvitationNewClickListenerDecline) {
            binding.invitation = item
            binding.clickListenerAccept = clickListenerAccept
            binding.clickListenerDecline = clickListenerDecline
            binding.executePendingBindings()
        }

        /* inflate layout here to encapsulate, onCreateViewHolder has nothing to know about inflation */
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListInvitationNewItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}

class InvitationNewDiffCallback : DiffUtil.ItemCallback<PlayerMeetObject>() {
    override fun areItemsTheSame(oldItem: PlayerMeetObject, newItem: PlayerMeetObject): Boolean {
        return oldItem.invitationId == newItem.invitationId
    }

    override fun areContentsTheSame(oldItem: PlayerMeetObject, newItem: PlayerMeetObject): Boolean {
        return oldItem == newItem
    }


}
class InvitationNewClickListenerAccept(val clickListener: (invitationId: Int?) -> Unit) {
    fun onClick(invitation: PlayerMeetObject) {
        return clickListener(invitation.invitationId)
    }
}

class InvitationNewClickListenerDecline(val clickListener: (invitationId: Int?) -> Unit) {
    fun onClick(invitation: PlayerMeetObject) {
        return clickListener(invitation.invitationId)
    }
}