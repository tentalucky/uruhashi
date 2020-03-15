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
import mahoroba.uruhashi.databinding.ListItemStadiumBinding
import mahoroba.uruhashi.domain.Stadium

class SelectStadiumDialogListAdapter(context: Context, owner: LifecycleOwner, private val viewModel: SelectStadiumDialogViewModel)
    : RecyclerView.Adapter<SelectStadiumDialogListAdapter.SelectStadiumListViewHolder>(){

    class SelectStadiumListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemStadiumBinding>(itemView)
    }

    var stadiums: List<Stadium>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.stadiums.observe(owner, Observer { it?.let { stadiums = it } })
    }

    private val mInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectStadiumListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_stadium, parent, false)
        return SelectStadiumListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SelectStadiumListViewHolder, position: Int) {
        val stadium = stadiums?.get(position)
        holder.binding?.stadium = stadium
        holder.binding?.executePendingBindings()
        holder.binding?.root?.setOnClickListener { viewModel.onSelected(stadium!!.id!!) }
    }

    override fun getItemCount(): Int {
        return if (stadiums == null) 0 else stadiums!!.count()
    }
}