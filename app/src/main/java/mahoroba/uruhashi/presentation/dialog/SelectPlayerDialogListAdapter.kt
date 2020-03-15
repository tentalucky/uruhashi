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
import mahoroba.uruhashi.databinding.ListItemPlayerBinding
import mahoroba.uruhashi.usecase.PlayerManagementUseCase

class SelectPlayerDialogListAdapter(context: Context, owner: LifecycleOwner, private val viewModel: SelectPlayerDialogViewModel)
    : RecyclerView.Adapter<SelectPlayerDialogListAdapter.SelectPlayerListViewHolder>(){

    class SelectPlayerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemPlayerBinding>(itemView)
    }

    var players: List<PlayerManagementUseCase.PlayerSummary>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.players.observe(owner, Observer { it?.let { players = it } })
    }

    private val mInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectPlayerListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_player, parent, false)
        return SelectPlayerListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SelectPlayerListViewHolder, position: Int) {
        val player = players?.get(position)!!
        holder.binding?.player = player
        holder.binding?.executePendingBindings()
        holder.binding?.root?.setOnClickListener { viewModel.onSelect(player.id) }
    }

    override fun getItemCount(): Int {
        return if (players == null) 0 else players!!.count()
    }
}
