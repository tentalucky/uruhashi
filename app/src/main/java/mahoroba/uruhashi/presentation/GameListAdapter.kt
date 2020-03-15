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
import mahoroba.uruhashi.databinding.ListItemGameBinding
import mahoroba.uruhashi.usecase.query.GameSummaryDto

class GameListAdapter(context: Context, owner: LifecycleOwner, private val viewModel: GameListViewModel)
    : RecyclerView.Adapter<GameListAdapter.GameListViewHolder>(){

    class GameListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemGameBinding>(itemView)
    }

    var games: List<GameSummaryDto>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.gameSummaryList.observe(owner, Observer<List<GameSummaryDto>>{ t ->
            t?.let { games = it }
        })
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_game, parent, false)
        return GameListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GameListViewHolder, position: Int) {
        val game = games?.get(position)
        holder.binding?.game = game
        holder.binding?.executePendingBindings()
        holder.binding?.root?.setOnClickListener{ viewModel.selectCommand(game!!.gameId) }
    }

    override fun getItemCount(): Int {
        return if (games == null) 0 else games!!.count()
    }
}