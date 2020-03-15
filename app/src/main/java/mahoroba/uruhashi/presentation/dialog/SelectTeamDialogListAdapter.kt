package mahoroba.uruhashi.presentation.dialog

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

class SelectTeamDialogListAdapter(context: Context, owner: LifecycleOwner, private val viewModel: SelectTeamDialogViewModel)
    : RecyclerView.Adapter<SelectTeamDialogListAdapter.SelectTeamListViewHolder>() {

    class SelectTeamListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemTeamBinding>(itemView)
    }

    var teams: List<TeamManagementUseCase.TeamSummary>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.teams.observe(owner, Observer { it?.let { teams = it } })
    }

    private val mInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectTeamListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_team, parent, false)
        return SelectTeamListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SelectTeamListViewHolder, position: Int) {
        val team = teams?.get(position)
        holder.binding?.teamProfile = team
        holder.binding?.executePendingBindings()
        holder.binding?.root?.setOnClickListener { viewModel.onSelected(team!!.id) }
    }

    override fun getItemCount(): Int {
        return if (teams == null) 0 else teams!!.count()
    }
}