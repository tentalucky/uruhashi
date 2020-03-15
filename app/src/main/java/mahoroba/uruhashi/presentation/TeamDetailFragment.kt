package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentTeamDetailBinding

class TeamDetailFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = TeamDetailFragment()
    }

    private lateinit var viewModel: TeamListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(TeamListViewModel::class.java)

        val binding = FragmentTeamDetailBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.vm = viewModel

        val adapter = TeamMemberListAdapter(context!!, this, viewModel)
        binding.memberListView.adapter = adapter
        binding.memberListView.layoutManager = LinearLayoutManager(context!!)

        return binding.root
    }


}
