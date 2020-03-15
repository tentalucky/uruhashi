package mahoroba.uruhashi.presentation.dialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.DialogSelectStadiumBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel
import kotlin.reflect.KClass

class SelectStadiumDialogFragment() : DialogFragment() {
    companion object {
        @JvmStatic
        fun <T : BaseViewModel> newInstance(callerViewModel: KClass<T>): SelectStadiumDialogFragment {
            val instance = SelectStadiumDialogFragment()
            instance.arguments = Bundle().apply {
                this.putString("class", callerViewModel.qualifiedName)
            }
            return instance
        }
    }

    private lateinit var viewModel: SelectStadiumDialogViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var listener: SelectStadiumDialogViewModel.Listener? = null
        if (parentFragment is BaseFragment) {
            if ((parentFragment as BaseFragment).viewModel is SelectStadiumDialogViewModel.Listener) {
                listener = (parentFragment as BaseFragment).viewModel as SelectStadiumDialogViewModel.Listener
            }
        }

        viewModel = ViewModelProviders
            .of(
                this,
                SelectStadiumDialogViewModel.Factory(activity!!.application, listener)
            )
            .get(SelectStadiumDialogViewModel::class.java)

        val binding = DataBindingUtil.inflate<DialogSelectStadiumBinding>(
            LayoutInflater.from(activity), R.layout.dialog_select_stadium, null, false
        )
        binding.vm = viewModel
        binding.lifecycleOwner = this

        binding.stadiumListView.layoutManager = LinearLayoutManager(context)
        binding.stadiumListView.adapter =
            SelectStadiumDialogListAdapter(context!!, this, viewModel)

        val builder = AlertDialog.Builder(context!!)
            .setView(binding.root)

        viewModel.dialogMethods.observe(this, Observer { it?.invoke(this) })

        return builder.create()
    }

}