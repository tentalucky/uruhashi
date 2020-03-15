package mahoroba.uruhashi.presentation

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
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
import mahoroba.uruhashi.databinding.ListItemPlayerBelongingEditBinding

class PlayerBelongingEditListAdapter(
    private val context: Context,
    private val owner: LifecycleOwner,
    val sourceData: LiveData<List<PlayerEditViewModel.PlayerBelongingEditItemViewModel>>
) :
    RecyclerView.Adapter<PlayerBelongingEditListAdapter.PlayerBelongingEditListViewHolder>() {

    class PlayerBelongingEditListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = DataBindingUtil.bind<ListItemPlayerBelongingEditBinding>(itemView)
    }

    private val mInflater = LayoutInflater.from(context)!!

    init {
        sourceData.observe(owner, Observer { notifyDataSetChanged() })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : PlayerBelongingEditListViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_player_belonging_edit, parent, false)
        return PlayerBelongingEditListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlayerBelongingEditListViewHolder, position: Int) {
        val belongingTo = sourceData.value?.get(position)

        holder.binding?.belongingTo = belongingTo

        belongingTo?.openUniformNumberInput?.observe(owner, Observer { callback ->
            val editText = EditText(context).apply {
                this.inputType = InputType.TYPE_CLASS_NUMBER
                this.filters = arrayOf(InputFilter.LengthFilter(3))
            }
            AlertDialog.Builder(context)
                .setView(editText)
                .setPositiveButton("OK") { _, _ -> callback?.invoke(editText.text.toString()) }
                .show()
        })
    }

    override fun getItemCount(): Int {
        return sourceData.value?.count() ?: 0
    }
}