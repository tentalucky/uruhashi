package mahoroba.uruhashi.presentation


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mahoroba.uruhashi.R

import mahoroba.uruhashi.databinding.FragmentPitchInputBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel

class PitchInputFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() = PitchInputFragment()
    }

    private lateinit var mViewModel: PitchInputViewModel
    override val viewModel: BaseViewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel = ViewModelProviders.of(activity!!).get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.playInputViewModel.pitchInputViewModel

        val binding = FragmentPitchInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = mViewModel

        registerObservers(binding)

        return binding.root
    }

    private fun registerObservers(binding: FragmentPitchInputBinding) {

        mViewModel.onOptionMenuOpen.observe(this, "tag", Observer {
            PopupMenu(context!!, binding.buttonOption).apply {
                setOnMenuItemClickListener {
                    mViewModel.onMenuSelected(it.itemId)
                    true
                }
                inflate(R.menu.pitch_input_option_menu)
                show()
            }
        })

    }
}
