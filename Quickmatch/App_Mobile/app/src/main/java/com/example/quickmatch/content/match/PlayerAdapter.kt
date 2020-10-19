package com.example.quickmatch.content.match

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quickmatch.R
import com.example.quickmatch.databinding.ListMeetsheetPlayerItemBinding
import com.example.quickmatch.network.PlayerObject

class PlayerAdapter(private var arrayMeetsheetRows: ArrayList<MeetSheetRow>) :
    ListAdapter<PlayerObject, PlayerAdapter.ViewHolder>(PlayerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        return holder.bind(item, arrayMeetsheetRows)
    }

    class ViewHolder private constructor(val binding: ListMeetsheetPlayerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /* bind view values do the data */
        fun bind(item: PlayerObject, arrayMeetsheetRows: ArrayList<MeetSheetRow>) {
            binding.player = item

            /* text watcher for the score edit text in order to store the player's score */

            binding.editScore.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val result = arrayMeetsheetRows.find { row -> row.id == item.id }
                    if (result == null) {
                        arrayMeetsheetRows.add(MeetSheetRow(item.id!!, s.toString().toInt(), -1))
                    } else {
                        arrayMeetsheetRows[arrayMeetsheetRows.indexOf(result)].goals =
                            s.toString().toInt()
                    }

                }
            })

            /* click listeners to chage players team on click on a button */

            binding.radioTeam1.setOnClickListener {
                PlayerRadioClickListener(arrayMeetsheetRows,
                    it as RadioButton,
                    it.context.getString(R.string.text_team1),
                    it.context.getString(R.string.text_team2)).onClick(item)
            }

            binding.radioTeam2.setOnClickListener {
                PlayerRadioClickListener(arrayMeetsheetRows,
                    it as RadioButton,
                    it.context.getString(R.string.text_team1),
                    it.context.getString(R.string.text_team2)).onClick(item)
            }

            binding.executePendingBindings()
        }

        companion object {
            /* inflate layout here to encapsulate, onCreateViewHolder has nothing to know about inflation */
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListMeetsheetPlayerItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class PlayerDiffCallback : DiffUtil.ItemCallback<PlayerObject>() {
    override fun areItemsTheSame(oldItem: PlayerObject, newItem: PlayerObject): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlayerObject, newItem: PlayerObject): Boolean {
        return oldItem == newItem
    }
}

class PlayerRadioClickListener(
    val arrayMeetsheetRows: ArrayList<MeetSheetRow>,
    val radioButton: RadioButton, val team1Text: String, val team2Text: String
) {

    fun onClick(player: PlayerObject) {
        /* search if the player has already a row in the array */
        val result = arrayMeetsheetRows.find { row -> row.id == player.id }

        if (result == null) { // create the row if not exists
            arrayMeetsheetRows.add(
                MeetSheetRow(
                    player.id!!, 0,
                    when (radioButton.text) {
                        team1Text -> 1
                        team2Text -> 2
                        else -> -1
                    }
                )
            )

        } else { // modify the existing row if exists
            arrayMeetsheetRows[arrayMeetsheetRows.indexOf(result)].team = when (radioButton.text) {
                team1Text -> 1
                team2Text -> 2
                else -> -1
            }
        }
    }
}


data class MeetSheetRow(
    val id: Int,
    var goals: Int,
    var team: Int
)