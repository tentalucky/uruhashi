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
import mahoroba.uruhashi.databinding.ListItemStadiumBinding
import mahoroba.uruhashi.domain.Stadium

class StadiumListAdapter(context: Context, owner: LifecycleOwner, private val viewModel: StadiumListViewModel)
    : RecyclerView.Adapter<StadiumListAdapter.StadiumListViewHolder>() {

    class StadiumListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemStadiumBinding>(itemView)
    }

    var stadiums: List<Stadium>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.allStadiums.observe(owner, Observer<List<Stadium>> { t ->
            t?.let { stadiums = it}
        })
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StadiumListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_stadium, parent, false)
        return StadiumListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StadiumListViewHolder, position: Int) {
        val stadium = stadiums?.get(position)
        holder.binding?.stadium = stadium
        holder.binding?.executePendingBindings()
        holder.binding?.root?.setOnClickListener { viewModel.select(position) }
    }

    override fun getItemCount(): Int {
        return if (stadiums == null) 0 else stadiums!!.count()
    }
}