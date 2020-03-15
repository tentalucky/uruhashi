package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.databinding.FragmentTeamListBinding

class TeamListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = TeamListFragment()
    }

    private lateinit var viewModel: TeamListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(TeamListViewModel::class.java)

        val binding = FragmentTeamListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.teamListView.layoutManager = LinearLayoutManager(context!!)
        binding.vm = viewModel

        val adapter = TeamListAdapter(context!!, this, viewModel)
        binding.teamListView.adapter = adapter

        return binding.root
    }


}
