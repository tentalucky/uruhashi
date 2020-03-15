package mahoroba.uruhashi.presentation


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentPlayInputBinding
import mahoroba.uruhashi.presentation.PlayInputViewModel.ActiveFragmentType.*
import mahoroba.uruhashi.presentation.base.BaseFragment

class PlayInputFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = PlayInputFragment()
    }

    private var referenceView : View? = null

    private lateinit var mViewModel: PlayInputViewModel
    override val viewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel = ViewModelProviders.of(activity!!).get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.playInputViewModel

        val binding = FragmentPlayInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = mViewModel

        referenceView = binding.reference

        viewModel.fragmentMethod.observe(this, Observer { it?.invoke(this) })
        viewModel.activeFragmentType.observe(this, Observer { t -> switchChildFragment(t!!) })

        switchEasyReferenceFragment()

        return binding.root
    }

    private fun switchChildFragment(type : PlayInputViewModel.ActiveFragmentType) {
        when (type) {
            PITCH_INPUT -> PitchInputFragment.newInstance()
            FOUL_BALL_INPUT -> FoulBallInputFragment.newInstance()
            BATTED_BALL_INPUT -> BattingInputFragment.newInstance()
            FIELD_PLAY_INPUT -> FieldPlayInputFragment.newInstance()
            OFFENCE_SUBSTITUTION -> OffenceSubstitutionInputFragment.newInstance()
            DEFENCE_SUBSTITUTION -> DefenseSubstitutionInputFragment.newInstance()
            EASY_REFERENCE -> EasyReferenceFragment.newInstance()
        }.let { t ->
            childFragmentManager.beginTransaction()
                .replace(R.id.content, t)
                .commit()
        }
    }

    private fun switchEasyReferenceFragment() {
        if (referenceView == null) return

        childFragmentManager.beginTransaction()
            .replace(R.id.reference, EasyReferenceFragment.newInstance())
            .commit()
    }
}
