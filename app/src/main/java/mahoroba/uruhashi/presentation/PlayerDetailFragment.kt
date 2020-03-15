package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentPlayerDetailBinding

class PlayerDetailFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = PlayerDetailFragment()
    }

    private lateinit var viewModel: PlayerListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(PlayerListViewModel::class.java)

        val binding = FragmentPlayerDetailBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.vm = viewModel

        val adapter = PlayerBelongingListAdapter(context!!, this, viewModel)
        binding.belongingListView.adapter = adapter
        binding.belongingListView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

}
