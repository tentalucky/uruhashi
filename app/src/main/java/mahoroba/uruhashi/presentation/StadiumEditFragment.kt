package mahoroba.uruhashi.presentation

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import mahoroba.uruhashi.databinding.FragmentStadiumEditBinding

class StadiumEditFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = StadiumEditFragment()
    }

    private lateinit var viewModel: StadiumEditViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(StadiumEditViewModel::class.java)

        val binding = FragmentStadiumEditBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.vm = viewModel

        return binding.root
    }
}
