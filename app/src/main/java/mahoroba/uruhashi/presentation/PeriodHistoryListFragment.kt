package mahoroba.uruhashi.presentation


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.FragmentPeriodHistoryListBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel

class PeriodHistoryListFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() = PeriodHistoryListFragment()
    }

    private lateinit var mViewModel: PeriodHistoryListViewModel
    override val viewModel: BaseViewModel
        get() = mViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parentViewModel = ViewModelProviders.of(activity!!).get(ScoreKeepingViewModel::class.java)
        mViewModel = parentViewModel.periodHistoryListViewModel

        val binding = FragmentPeriodHistoryListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = mViewModel
        binding.periodListView.layoutManager = LinearLayoutManager(context!!)

        val adapter = PeriodHistoryListAdapter(context!!, this, mViewModel)
        binding.periodListView.adapter = adapter

        return binding.root
    }
}
