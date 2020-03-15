package mahoroba.uruhashi.presentation

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import kotlinx.android.synthetic.main.popup_select_operation_to_substitution.view.*
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.ListItemPeriodHistoryBinding
import mahoroba.uruhashi.presentation.utility.dpToPx
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class PeriodHistoryListAdapter(
    private val context: Context,
    private val owner: LifecycleOwner,
    private val viewModel: PeriodHistoryListViewModel
) :
    RecyclerView.Adapter<PeriodHistoryListAdapter.PeriodHistoryViewHolder>() {

    class PeriodHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = DataBindingUtil.bind<ListItemPeriodHistoryBinding>(itemView)
    }

    private var itemCount: Int = 0

    private var periods: List<ScoreKeepingUseCase.PeriodDto>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        viewModel.itemCount.observe(owner, Observer { t ->
            itemCount = t ?: 0
            notifyDataSetChanged()
        })
    }

    private val mInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeriodHistoryViewHolder {
        val itemView = mInflater.inflate(R.layout.list_item_period_history, parent, false)
        return PeriodHistoryViewHolder(itemView)
    }

    override fun getItemCount() = itemCount

    override fun onBindViewHolder(holder: PeriodHistoryViewHolder, position: Int) {
        viewModel.getItemAt(position).let { vm ->
            holder.binding?.vm = vm
            vm.requireShowPopupMenu.observe(owner, Observer {
                val popup = PopupWindow(context).apply {
                    val content = LayoutInflater.from(context)
                        .inflate(R.layout.popup_select_operation_to_substitution, null)
                    this.contentView = content
                    this.isOutsideTouchable = true
                    this.isFocusable = true
                    this.isTouchable = true
                    this.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    this.width = context.dpToPx(240f).toInt()

                    content.insertOffenceSubstitutionButton?.setOnClickListener {
                        vm.insertOffenceSubstitution()
                        this.dismiss()
                    }
                    content.insertDefenceSubstitutionButton?.setOnClickListener {
                        vm.insertDefenceSubstitution()
                        this.dismiss()
                    }
                    content.modifySubstitutionButton?.setOnClickListener {
                        vm.modifyThisSubstitution()
                        this.dismiss()
                    }
                    content.deleteSubstitutionButton?.setOnClickListener {
                        vm.deleteThisSubstitution()
                        this.dismiss()
                    }
                }

                val xOffset = ((holder.binding?.root?.width ?: 0) - popup.width) / 2
                popup.showAsDropDown(
                    holder.itemView,
                    xOffset,
                    context.dpToPx(-48f).toInt()
                )
            })
        }
    }
}