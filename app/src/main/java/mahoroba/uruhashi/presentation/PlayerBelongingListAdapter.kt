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
import mahoroba.uruhashi.databinding.ListItemPlayerBelongingBinding
import mahoroba.uruhashi.usecase.query.PlayerBelongingInfoDto

class PlayerBelongingListAdapter(context: Context, owner: LifecycleOwner, private val viewModel: PlayerListViewModel)
    : RecyclerView.Adapter<PlayerBelongingListAdapter.PlayerBelongingListViewHolder>(){

    class PlayerBelongingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemPlayerBelongingBinding>(itemView)
    }

    var belongings: List<PlayerBelongingInfoDto>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.selectedPlayerBelonging.observe(owner, Observer<List<PlayerBelongingInfoDto>> { t ->
            t.let { belongings = t }
        })
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerBelongingListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_player_belonging, parent, false)
        return PlayerBelongingListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlayerBelongingListViewHolder, position: Int) {
        val belonging = belongings?.get(position)
        holder.binding?.belonging = belonging
        holder.binding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (belongings == null) 0 else belongings!!.count()
    }
}