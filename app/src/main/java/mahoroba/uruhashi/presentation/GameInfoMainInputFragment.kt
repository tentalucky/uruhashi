package mahoroba.uruhashi.presentation


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentGameInfoMainInputBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel

class GameInfoMainInputFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = GameInfoMainInputFragment()
    }

    private lateinit var mViewModel: GameInfoInputViewModel
    override val viewModel: BaseViewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel: ScoreKeepingViewModel = ViewModelProviders
            .of(activity!!)
            .get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.gameInfoInputViewModel!!

        val binding = FragmentGameInfoMainInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = mViewModel

        return binding.root
    }

}
