package mahoroba.uruhashi.presentation


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentDefenseSubstitutionInputBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.dialog.SelectPlayerInGameDialogFragment
import mahoroba.uruhashi.presentation.dialog.SelectPlayerInGameDialogViewModel
import mahoroba.uruhashi.presentation.dialog.SelectPositionDialogFragment

class DefenseSubstitutionInputFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = DefenseSubstitutionInputFragment()
    }

    private lateinit var mViewModel: DefenseSubstitutionInputViewModel
    override val viewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel =
            ViewModelProviders.of(activity!!).get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.playInputViewModel.defenseSubstitutionInputViewModel

        val binding = FragmentDefenseSubstitutionInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = mViewModel

        registerObservers()

        return binding.root
    }

    private fun registerObservers() {
        mViewModel.onOpenSelectPositionDialog.observe(this, "tag", Observer {
            SelectPositionDialogFragment
                .newInstance(mViewModel::class, it?.first ?: false)
                .show(childFragmentManager, it?.second)
        })

        mViewModel.onOpenSelectPlayerDialog.observe(this, "tag", Observer {
            SelectPlayerInGameDialogFragment
                .newInstance(mViewModel::class, it?.first)
                .show(childFragmentManager, it?.second)
        })
    }
}
