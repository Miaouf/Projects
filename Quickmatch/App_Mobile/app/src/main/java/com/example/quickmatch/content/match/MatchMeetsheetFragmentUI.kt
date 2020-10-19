package com.example.quickmatch.content.match


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.quickmatch.BaseFragment
import com.example.quickmatch.R
import com.example.quickmatch.content.club.RequestStatus
import com.example.quickmatch.databinding.FragmentMatchMeetsheetBinding
import timber.log.Timber

class MatchMeetsheetFragmentUI : BaseFragment() {


    private lateinit var viewModel: MatchMeetsheetFragmentViewModel
    private lateinit var viewModelFactory: MatchMeetsheetFragmentViewModelFactory

    private val args: MatchMeetsheetFragmentUIArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding : FragmentMatchMeetsheetBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_match_meetsheet, container, false)
        viewModelFactory = MatchMeetsheetFragmentViewModelFactory(args.matchId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MatchMeetsheetFragmentViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        var arrayMeetSheetRows : ArrayList<MeetSheetRow> = arrayListOf()

        val adapter = PlayerAdapter(arrayMeetSheetRows)

        binding.listPlayers.adapter = adapter

        var team1_won: Boolean
        var team2_won: Boolean

        binding.buttonSendMeetsheet.setOnClickListener {

            if (binding.editScoreTeam1.text.toString() == ""
                || binding.editScoreTeam2.text.toString() == "") {

                Toast.makeText(context, "Vous devez encore remplir le score du match !", Toast.LENGTH_LONG).show()

            } else if (arrayMeetSheetRows.filter{ row -> row.team == 1 }.sumBy { row -> row.goals }
                != binding.editScoreTeam1.text.toString().toInt()
                || arrayMeetSheetRows.filter{ row -> row.team == 2 }.sumBy { row -> row.goals }
                != binding.editScoreTeam2.text.toString().toInt()
            ) {

                Toast.makeText(context, "Le score et les statistiques ne concordent pas, attention aux scores et équipes choisis", Toast.LENGTH_LONG).show()

            } else if (arrayMeetSheetRows.size < viewModel.players.value!!.size) {

                Toast.makeText(context, "Il manque les statistiques d'un ou plusieurs joueurs", Toast.LENGTH_LONG).show()

            } else if (arrayMeetSheetRows.find { row -> row.team == -1 } != null) {

                Toast.makeText(context, "Il reste un ou plusiuers joueurs sans équipe", Toast.LENGTH_LONG).show()

            } else {
                val team1_score = binding.editScoreTeam1.text.toString().toInt()
                val team2_score = binding.editScoreTeam2.text.toString().toInt()

                team1_won = team1_score > team2_score
                team2_won = team2_score > team1_score

                viewModel.makeMeetsheet(team1_won, team2_won, team1_score, team2_score, arrayMeetSheetRows)
                Timber.i(arrayMeetSheetRows.toString())
            }

        }

        /* Get the players Objects when we have all ids */
        viewModel.getPlayersMeetStatus.observe(viewLifecycleOwner, Observer {
            if(it == RequestStatus.DONE) viewModel.getPlayers()
        })

        /* submit recycler list */
        viewModel.players.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        /* work around to display asap the players list (got asynchronously) */
        viewModel.getPlayerStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it == RequestStatus.DONE) adapter.notifyDataSetChanged()
            }
        })

        viewModel.playersUpdated.observe(viewLifecycleOwner, Observer {
            if (it > 0 && it == viewModel.players.value!!.size && viewModel.sheetsCreated.value == it) {
                viewModel.updateMeetStatus()
                viewModel.resetCount()
            }

        })

        viewModel.sheetsCreated.observe(viewLifecycleOwner, Observer {
            if (it > 0 && it == viewModel.players.value!!.size && viewModel.playersUpdated.value == it) {
                viewModel.updateMeetStatus()
                viewModel.resetCount()
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getActionBar()?.title = "Feuille de match"
    }

}
