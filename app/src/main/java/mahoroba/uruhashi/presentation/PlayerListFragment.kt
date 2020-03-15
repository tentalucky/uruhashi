package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.databinding.FragmentPlayerListBinding

class PlayerListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = PlayerListFragment()
    }

    private lateinit var viewModel: PlayerListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(PlayerListViewModel::class.java)

        val binding = FragmentPlayerListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.playerListView.layoutManager = LinearLayoutManager(context!!)
        binding.vm = viewModel

        val adapter = PlayerListAdapter(context!!, this, viewModel)
        binding.playerListView.adapter = adapter

        return binding.root
    }

}
