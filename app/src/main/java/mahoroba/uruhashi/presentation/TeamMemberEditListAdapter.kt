package mahoroba.uruhashi.presentation

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.ListItemTeamMemberEditBinding

class TeamMemberEditListAdapter(private val context: Context, owner: LifecycleOwner, private val viewModel: TeamMemberEditViewModel)
    : RecyclerView.Adapter<TeamMemberEditListAdapter.TeamMemberEditListViewHolder>(){

    class TeamMemberEditListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemTeamMemberEditBinding>(itemView)
    }

    var members: List<TeamMemberEditViewModel.TeamMemberInfo>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.members.observe(owner, Observer<List<TeamMemberEditViewModel.TeamMemberInfo>> { t ->
            t?.let { members = t }
        })
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamMemberEditListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_team_member_edit, parent, false)
        return TeamMemberEditListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TeamMemberEditListViewHolder, position: Int) {
        val member = members?.get(position)

        holder.binding?.member = member

        holder.binding?.textUniformNumber?.setOnClickListener {
            val editText = EditText(context).apply {
                this.inputType = InputType.TYPE_CLASS_NUMBER
                this.filters = arrayOf( InputFilter.LengthFilter(3) )
            }
            AlertDialog.Builder(context)
                .setView(editText)
                .setPositiveButton("OK") { dlg, i ->
                    viewModel.changeUniformNumber(member!!.playerId, editText.text.toString()) }
                .show()
        }

        holder.binding?.buttonDeleteOrCancel?.setOnClickListener {
            if (member?.removing == true) {
                viewModel.cancelToDeletePlayer(member.playerId)
            } else {
                viewModel.deletePlayer(member!!.playerId)
            }
        }

        holder.binding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (members == null) 0 else members!!.count()
    }
}