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
import mahoroba.uruhashi.databinding.DialogSelectPlayerInGameBinding
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel
import kotlin.reflect.KClass

class SelectPlayerInGameDialogFragment : DialogFragment() {
    companion object {
        @JvmStatic
        fun <T : BaseViewModel> newInstance(callerViewModel: KClass<T>, teamId: ID?) : SelectPlayerInGameDialogFragment {
            val instance = SelectPlayerInGameDialogFragment()
            instance.arguments = Bundle().apply {
                this.putString("class", callerViewModel.qualifiedName)
                this.putSerializable("teamId", teamId)
            }
            return instance
        }
    }

    private lateinit var viewModel: SelectPlayerInGameDialogViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val teamId = arguments!!.getSerializable("teamId") as ID?

        var listener : SelectPlayerInGameDialogViewModel.Listener? = null
        if (parentFragment is BaseFragment) {
            if ((parentFragment as BaseFragment).viewModel is SelectPlayerInGameDialogViewModel.Listener) {
                listener = (parentFragment as BaseFragment).viewModel as SelectPlayerInGameDialogViewModel.Listener
            }
        }

        viewModel = ViewModelProviders
            .of(this,
                SelectPlayerInGameDialogViewModel.Factory(
                    activity!!.application,
                    teamId,
                    tag ?: "",
                    listener
                )
            )
            .get(SelectPlayerInGameDialogViewModel::class.java)

        val binding = DataBindingUtil.inflate<DialogSelectPlayerInGameBinding>(
            LayoutInflater.from(activity), R.layout.dialog_select_player_in_game, null, false)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        binding.playerListView.layoutManager = LinearLayoutManager(context)
        binding.playerListView.adapter =
            SelectPlayerInGameDialogListAdapter(context!!, this, viewModel)

        val builder = AlertDialog.Builder(context!!)
            .setView(binding.root)

        viewModel.dialogMethods.observe(this, Observer { it?.invoke(this) })

        return builder.create()
    }
}