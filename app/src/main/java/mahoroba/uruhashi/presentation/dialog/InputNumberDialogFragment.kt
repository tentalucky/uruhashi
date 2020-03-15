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
import mahoroba.uruhashi.databinding.DialogInputNumberBinding
import mahoroba.uruhashi.presentation.base.BaseFragment

class InputNumberDialogFragment : DialogFragment() {
    companion object {
        @JvmStatic
        fun newInstance(initialValue: Int?): InputNumberDialogFragment {
            val instance = InputNumberDialogFragment()
            instance.arguments = Bundle().apply {
                if (initialValue != null) this.putInt("initialValue", initialValue)
            }
            return instance
        }
    }

    private lateinit var viewModel: InputNumberDialogViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val initialValue: Int? = arguments?.getInt("initialValue")

        var listener: InputNumberDialogViewModel.Listener? = null
        if (parentFragment is BaseFragment) {
            if ((parentFragment as BaseFragment).viewModel is InputNumberDialogViewModel.Listener) {
                listener = (parentFragment as BaseFragment).viewModel as InputNumberDialogViewModel.Listener
            }
        }

        viewModel = ViewModelProviders.of(
            this,
            InputNumberDialogViewModel.Factory(
                activity!!.application,
                tag ?: "",
                listener,
                initialValue
            )
        ).get(InputNumberDialogViewModel::class.java)

        val binding = DataBindingUtil.inflate<DialogInputNumberBinding>(
            LayoutInflater.from(activity), R.layout.dialog_input_number, null, false
        ).apply {
            this.vm = viewModel
            this.lifecycleOwner = this@InputNumberDialogFragment
        }

        val builder = AlertDialog.Builder(context!!).setView(binding.root)

        viewModel.closeDialogEvent.observe(this, Observer { dismiss() })

        return builder.create()
    }
}