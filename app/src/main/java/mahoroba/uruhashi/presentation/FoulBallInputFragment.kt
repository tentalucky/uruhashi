package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.databinding.FragmentFoulBallInputBinding
import mahoroba.uruhashi.presentation.base.BaseFragment

class FoulBallInputFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = FoulBallInputFragment()
    }

    private lateinit var mViewModel: FoulBallInputViewModel
    override val viewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel = ViewModelProviders.of(activity!!).get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.playInputViewModel.foulBallInputViewModel

        val binding = FragmentFoulBallInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = mViewModel

        return binding.root
    }

}
