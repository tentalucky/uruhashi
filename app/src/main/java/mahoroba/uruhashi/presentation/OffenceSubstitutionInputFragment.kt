package mahoroba.uruhashi.presentation


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentOffenceSubstitutionInputBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.dialog.SelectPlayerInGameDialogFragment

class OffenceSubstitutionInputFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() = OffenceSubstitutionInputFragment()
    }

    private lateinit var mViewModel: OffenceSubstitutionInputViewModel
    override val viewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel =
            ViewModelProviders.of(activity!!).get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.playInputViewModel.offenseSubstitutionInputViewModel

        val binding = FragmentOffenceSubstitutionInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = mViewModel

        registerObservers()

        return binding.root
    }

    private fun registerObservers() {
        mViewModel.onOpenSelectPlayerDialog.observe(this, "tag", Observer {
            SelectPlayerInGameDialogFragment
                .newInstance(mViewModel::class, it?.first)
                .show(childFragmentManager, it?.second)
        })
    }

}
