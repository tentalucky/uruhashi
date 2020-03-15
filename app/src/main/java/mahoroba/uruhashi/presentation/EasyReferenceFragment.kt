package mahoroba.uruhashi.presentation


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mahoroba.uruhashi.databinding.FragmentEasyReferenceBinding

class EasyReferenceFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = EasyReferenceFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentEasyReferenceBinding.inflate(inflater, container, false)
        binding.pager.adapter = EasyReferencePagerAdapter(activity!!.supportFragmentManager)

        return binding.root
    }

    private inner class EasyReferencePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> ScoreBoardFragment.newInstance()
            else -> PeriodHistoryListFragment.newInstance()
        }
    }
}
