package mahoroba.uruhashi.presentation

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentGameEndingBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel

class GameEndingFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = GameEndingFragment()
    }

    private var referenceView : View? = null

    private lateinit var mViewModel: GameEndingViewModel
    override val viewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel = ViewModelProviders.of(activity!!).get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.gameEndingViewModel

        val binding = FragmentGameEndingBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = mViewModel

        referenceView = binding.reference

        switchEasyReferenceFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.content, GameResultInputFragment.newInstance())
            .commit()

        return binding.root
    }

    private fun switchEasyReferenceFragment() {
        if (referenceView == null) return

        childFragmentManager.beginTransaction()
            .replace(R.id.reference, EasyReferenceFragment.newInstance())
            .commit()
    }
}