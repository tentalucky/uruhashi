package mahoroba.uruhashi.presentation.dialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.DialogSelectTeamBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.base.ViewModelUser
import kotlin.reflect.KClass

class SelectTeamDialogFragment : DialogFragment() {
    companion object {
        @JvmStatic
        fun <T : BaseViewModel> newInstance(callerViewModel: KClass<T>): SelectTeamDialogFragment {
            val instance = SelectTeamDialogFragment()
            instance.arguments = Bundle().apply {
                this.putString("class", callerViewModel.qualifiedName)
            }
            return instance
        }
    }

    private lateinit var viewModel: SelectTeamDialogViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener =
            (parentFragment as? ViewModelUser)?.viewModel as? SelectTeamDialogViewModel.Listener

        viewModel = ViewModelProviders
            .of(
                this,
                SelectTeamDialogViewModel.Factory(
                    activity!!.application, tag, listener
                )
            )
            .get(SelectTeamDialogViewModel::class.java)

        val binding = DataBindingUtil.inflate<DialogSelectTeamBinding>(
            LayoutInflater.from(activity), R.layout.dialog_select_team, null, false
        )
        binding.vm = viewModel
        binding.lifecycleOwner = this

        binding.teamListView.layoutManager = LinearLayoutManager(context)
        binding.teamListView.adapter =
            SelectTeamDialogListAdapter(context!!, this, viewModel)

        val builder = AlertDialog.Builder(context!!)
            .setView(binding.root)

        viewModel.dialogMethods.observe(this, Observer { it?.invoke(this) })

        return builder.create()
    }
}