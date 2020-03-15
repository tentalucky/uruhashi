package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.databinding.FragmentTeamMemberEditBinding

class TeamMemberEditFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = TeamMemberEditFragment()
    }

    private lateinit var viewModel: TeamMemberEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(TeamMemberEditViewModel::class.java)

        val binding = FragmentTeamMemberEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        val adapter = TeamMemberEditListAdapter(context!!, this, viewModel)
        binding.memberListView.adapter = adapter
        binding.memberListView.layoutManager = LinearLayoutManager(context!!)

        return binding.root
    }

}
