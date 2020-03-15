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
import mahoroba.uruhashi.databinding.ListItemFieldPlayBinding

class FieldPlayListAdapter(context: Context, owner: LifecycleOwner, viewModel: FieldPlayInputViewModel)
    : RecyclerView.Adapter<FieldPlayListAdapter.FieldPlayListViewHolder>(){

    class FieldPlayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemFieldPlayBinding>(itemView)
    }

    var fieldPlayList: List<FieldPlayInputViewModel.FieldPlaySet>? = null
        private set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.fieldPlaySetList.observe(owner, Observer { t -> t?.let { fieldPlayList = t } })
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldPlayListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_field_play, parent, false)
        return FieldPlayListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (fieldPlayList == null) 0 else fieldPlayList!!.count()
    }

    override fun onBindViewHolder(holder: FieldPlayListViewHolder, postition: Int) {
        val fieldPlaySet = fieldPlayList?.reversed()?.get(postition)
        holder.binding?.play = fieldPlaySet
        holder.binding?.executePendingBindings()
    }
}