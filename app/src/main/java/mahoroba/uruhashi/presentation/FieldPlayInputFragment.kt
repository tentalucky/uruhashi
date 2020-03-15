package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.databinding.FragmentFieldPlayInputBinding
import mahoroba.uruhashi.presentation.base.BaseFragment

class FieldPlayInputFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = FieldPlayInputFragment()
    }

    private lateinit var mViewModel: FieldPlayInputViewModel
    override val viewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel = ViewModelProviders.of(activity!!).get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.playInputViewModel.fieldPlayInputViewModel

        val binding = FragmentFieldPlayInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.playHistoryView.layoutManager = LinearLayoutManager(context!!)
        binding.vm = mViewModel

        val adapter = FieldPlayListAdapter(context!!, this, viewModel)
        binding.playHistoryView.adapter = adapter

        return binding.root
    }
}
