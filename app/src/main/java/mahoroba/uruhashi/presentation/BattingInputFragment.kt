package mahoroba.uruhashi.presentation


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.databinding.FragmentBattingInputBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.dialog.InputNumberDialogFragment

class BattingInputFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = BattingInputFragment()
    }

    private lateinit var mViewModel: BattingInputViewModel
    override val viewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel = ViewModelProviders.of(activity!!).get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.playInputViewModel.battingInputViewModel

        mViewModel.openNumberInputDialogEvent.observe(
            this,
            Observer { openInputNumberDialog(it!!.tag, it.initialValue) })

        val binding = FragmentBattingInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = mViewModel

        return binding.root
    }

    private fun openInputNumberDialog(tag: String, initialValue: Int?) {
        InputNumberDialogFragment.newInstance(initialValue)
            .show(childFragmentManager, tag)
    }

}
