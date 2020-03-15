package mahoroba.uruhashi.presentation

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.ListItemTeamMemberBinding
import mahoroba.uruhashi.usecase.query.TeamMemberInfoDto

class TeamMemberListAdapter(context: Context, owner: LifecycleOwner, private val viewModel: TeamListViewModel)
    : RecyclerView.Adapter<TeamMemberListAdapter.TeamMemberListViewHolder>(){

    class TeamMemberListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemTeamMemberBinding>(itemView)
    }

    var members: List<TeamMemberInfoDto>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.selectedTeamMembers.observe(owner, Observer<List<TeamMemberInfoDto>> { t ->
            t?.let { members = t }
        })
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamMemberListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_team_member, parent, false)
        return TeamMemberListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TeamMemberListViewHolder, position: Int) {
        val member = members?.get(position)
        holder.binding?.member = member
        holder.binding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (members == null) 0 else members!!.count()
    }
}