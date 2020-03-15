package mahoroba.uruhashi.presentation.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.DialogSelectPlayerBinding
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.ViewModelUser

class SelectPlayerDialogFragment : DialogFragment() {
    private lateinit var viewModel: SelectPlayerDialogViewModel

    interface OnSelectListener {
        fun onSelectPlayer(playerId: ID)
    }

    companion object {
        fun newInstance() : SelectPlayerDialogFragment
            = SelectPlayerDialogFragment()
        fun newInstance(target: BaseFragment, requestCode: Int) : SelectPlayerDialogFragment
            = SelectPlayerDialogFragment().apply { this.setTargetFragment(target, requestCode) }
    }

    private var mAttachedContext: Context? = null

    val caller : ViewModelUser?
        get() {
            if (targetFragment != null && targetFragment is ViewModelUser) return targetFragment as ViewModelUser
            if (mAttachedContext != null && mAttachedContext is ViewModelUser) return mAttachedContext as ViewModelUser
            return null
        }

    var onSelect : ((String) -> Unit)? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mAttachedContext = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProviders.of(this).get(SelectPlayerDialogViewModel::class.java)

        val binding = DataBindingUtil.inflate<DialogSelectPlayerBinding>(
            LayoutInflater.from(activity), R.layout.dialog_select_player, null, false)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        binding.playerListView.layoutManager = LinearLayoutManager(context)
        binding.playerListView.adapter =
            SelectPlayerDialogListAdapter(context!!, this, viewModel)

        val builder = AlertDialog.Builder(context)
            .setView(binding.root)

        viewModel.dialogMethod.observe(this, Observer { it?.invoke(this) })

        return builder.create()
    }
}