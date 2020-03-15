package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentGameDetailBinding

class GameDetailFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = GameDetailFragment()
    }

    private lateinit var viewModel: GameListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(GameListViewModel::class.java)

        val binding = FragmentGameDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        return binding.root
    }
}
