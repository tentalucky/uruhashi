package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentGameListBinding

class GameListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = GameListFragment()
    }

    private lateinit var viewModel: GameListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(GameListViewModel::class.java)

        val binding = FragmentGameListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.gameListView.layoutManager = LinearLayoutManager(context!!)

        val adapter = GameListAdapter(context!!, this, viewModel)
        binding.gameListView.adapter = adapter

        return binding.root
    }


}
