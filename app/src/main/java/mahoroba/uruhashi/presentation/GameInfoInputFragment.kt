package mahoroba.uruhashi.presentation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mahoroba.uruhashi.databinding.FragmentGameInfoInputBinding
import mahoroba.uruhashi.presentation.base.BaseFragment

class GameInfoInputFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = GameInfoInputFragment()
    }

    private lateinit var mViewModel: GameInfoInputViewModel
    override val viewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel = ViewModelProviders.of(activity!!).get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.gameInfoInputViewModel!!

        val binding = FragmentGameInfoInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = mViewModel

        viewModel.fragmentMethod.observe(this, Observer { it?.invoke(this) })
        viewModel.fragmentTransit.observe(this, Observer { it?.invoke(this, binding.contents.id) })

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction().apply {
                this.replace(binding.contents.id, GameInfoMainInputFragment.newInstance())
                this.setTransition(FragmentTransaction.TRANSIT_NONE)
                this.commit()
            }
        }

        return binding.root
    }

}
