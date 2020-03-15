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
import mahoroba.uruhashi.databinding.ListItemPlayerBinding
import mahoroba.uruhashi.usecase.PlayerManagementUseCase

class PlayerListAdapter(context: Context, owner: LifecycleOwner, private val viewModel: PlayerListViewModel)
    : RecyclerView.Adapter<PlayerListAdapter.PlayerListViewHolder>() {

    class PlayerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemPlayerBinding>(itemView)
    }

    var players: List<PlayerManagementUseCase.PlayerSummary>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.playerSummaryList.observe(owner, Observer<List<PlayerManagementUseCase.PlayerSummary>> { t ->
            t?.let { players = it }
        })
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_player, parent, false)
        return PlayerListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlayerListViewHolder, position: Int) {
        val player = players?.get(position)
        holder.binding?.player = player
        holder.binding?.executePendingBindings()
        holder.binding?.root?.setOnClickListener { viewModel.selectCommand(player!!.id) }
    }

    override fun getItemCount(): Int {
        return if (players == null) 0 else players!!.count()
    }

}