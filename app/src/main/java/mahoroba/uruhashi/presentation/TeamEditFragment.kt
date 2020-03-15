package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentTeamEditBinding

class TeamEditFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = TeamEditFragment()
    }

    private lateinit var viewModel: TeamEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(TeamEditViewModel::class.java)

        val binding = FragmentTeamEditBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.vm = viewModel

        return binding.root
    }


}
