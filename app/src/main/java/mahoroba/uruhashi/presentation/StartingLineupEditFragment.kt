package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentStartingLineupEditBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel

class StartingLineupEditFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = StartingLineupEditFragment()
    }

    private lateinit var mViewModel: GameInfoInputViewModel
    override val viewModel: BaseViewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel = ViewModelProviders
            .of(activity!!)
            .get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.gameInfoInputViewModel!!

        val binding = FragmentStartingLineupEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = mViewModel

        return binding.root
    }
}
