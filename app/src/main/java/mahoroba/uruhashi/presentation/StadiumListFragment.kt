package mahoroba.uruhashi.presentation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentStadiumListBinding
import mahoroba.uruhashi.domain.Stadium

class StadiumListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = StadiumListFragment()
    }

    private lateinit var viewModel: StadiumListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(StadiumListViewModel::class.java)

        // Inflate the layout for this fragment
        val binding = FragmentStadiumListBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.stadiumListView.layoutManager = LinearLayoutManager(context!!)
        binding.vm = viewModel

        val adapter = StadiumListAdapter(context!!, this, viewModel)
        binding.stadiumListView.adapter = adapter

        return binding.root
    }
}
