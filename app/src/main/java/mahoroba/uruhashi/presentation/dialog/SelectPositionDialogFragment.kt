package mahoroba.uruhashi.presentation.dialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.DialogSelectPositionBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel
import kotlin.reflect.KClass

class SelectPositionDialogFragment : DialogFragment() {
    companion object {
        @JvmStatic
        fun <T : BaseViewModel> newInstance(callerViewModel: KClass<T>, enableDH: Boolean) : SelectPositionDialogFragment {
            val instance = SelectPositionDialogFragment()
            instance.arguments = Bundle().apply {
                this.putString("class", callerViewModel.qualifiedName)
                this.putBoolean("enableDH", enableDH)
            }
            return instance
        }
    }

    private lateinit var viewModel: SelectPositionDialogViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val enableDH = arguments!!.getBoolean("enableDH")

        var listener : SelectPositionDialogViewModel.Listener? = null
        if (parentFragment is BaseFragment) {
            if ((parentFragment as BaseFragment).viewModel is SelectPositionDialogViewModel.Listener) {
                listener = (parentFragment as BaseFragment).viewModel as SelectPositionDialogViewModel.Listener
            }
        }

        viewModel = ViewModelProviders
            .of(this,
                SelectPositionDialogViewModel.Factory(
                    activity!!.application,
                    enableDH,
                    tag ?: "",
                    listener
                )
            )
            .get(SelectPositionDialogViewModel::class.java)

        val binding = DataBindingUtil.inflate<DialogSelectPositionBinding>(
            LayoutInflater.from(activity), R.layout.dialog_select_position, null, false)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        val builder = AlertDialog.Builder(context!!)
            .setView(binding.root)

        viewModel.dialogMethods.observe(this, Observer { it?.invoke(this) })

        return builder.create()
    }
}