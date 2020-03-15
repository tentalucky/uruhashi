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
import mahoroba.uruhashi.databinding.ListItemPlayerInGameBinding
import mahoroba.uruhashi.usecase.SearchPlayerUseCase

class SelectPlayerInGameDialogListAdapter(
    context: Context, owner: LifecycleOwner, private val viewModel: SelectPlayerInGameDialogViewModel)
    : RecyclerView.Adapter<SelectPlayerInGameDialogListAdapter.SelectPlayerInGameDialogListViewHolder>(){

    class SelectPlayerInGameDialogListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemPlayerInGameBinding>(itemView)
    }

    var players: List<SearchPlayerUseCase.PlayerSummary>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.filteredPlayerList.observe(owner, Observer { it?.let { players = it } })
    }

    private val mInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectPlayerInGameDialogListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_player_in_game, parent, false)
        return SelectPlayerInGameDialogListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SelectPlayerInGameDialogListViewHolder, position: Int) {
        val player = players?.get(position)!!
        holder.binding?.player = player
        holder.binding?.executePendingBindings()
        holder.binding?.root?.setOnClickListener { viewModel.onSelect(player) }
    }

    override fun getItemCount(): Int {
        return if (players == null) 0 else players!!.count()
    }
}