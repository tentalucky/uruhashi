package mahoroba.uruhashi.presentation


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentStadiumDetailBinding

class StadiumDetailFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = StadiumDetailFragment()
    }

    private lateinit var viewModel: StadiumListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(StadiumListViewModel::class.java)

        // Inflate the layout for this fragment
        val binding = FragmentStadiumDetailBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.vm = viewModel

        return binding.root
    }

}
