package mahoroba.uruhashi.presentation

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.ListItemTeamBinding
import mahoroba.uruhashi.usecase.TeamManagementUseCase

class TeamListAdapter(context: Context, owner: LifecycleOwner, private val viewModel: TeamListViewModel)
    : RecyclerView.Adapter<TeamListAdapter.TeamListViewHolder>() {

    class TeamListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemTeamBinding>(itemView)
    }

    var teams: List<TeamManagementUseCase.TeamSummary>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.teamSummaryList.observe(owner, Observer<List<TeamManagementUseCase.TeamSummary>> { t ->
            t?.let { teams = it }
        })
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_team, parent, false)
        return TeamListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TeamListViewHolder, position: Int) {
        val team = teams?.get(position)
        holder.binding?.teamProfile = team
        holder.binding?.executePendingBindings()
        holder.binding?.root?.setOnClickListener { viewModel.selectCommand(team!!.id) }
    }

    override fun getItemCount(): Int {
        return if (teams == null) 0 else teams!!.count()
    }
}