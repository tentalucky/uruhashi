package mahoroba.uruhashi.presentation


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.databinding.FragmentPlayerEditBinding
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.dialog.SelectTeamDialogFragment

class PlayerEditFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = PlayerEditFragment()
    }

    override lateinit var viewModel: PlayerEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(PlayerEditViewModel::class.java)

        val binding = FragmentPlayerEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        val adapter = PlayerBelongingEditListAdapter(context!!, this, viewModel.belongings)
        binding.belongingListView.adapter = adapter
        binding.belongingListView.layoutManager = LinearLayoutManager(context!!)

        viewModel.openSelectTeamDialog.observe(this, Observer {
            SelectTeamDialogFragment.newInstance(viewModel::class)
                .show(childFragmentManager, "")
        })

        return binding.root
    }

}
