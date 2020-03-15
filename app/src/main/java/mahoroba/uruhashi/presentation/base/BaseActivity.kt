package mahoroba.uruhashi.presentation.base

import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), ViewModelUser {

    override fun onBackPressed() {
        for (frag in supportFragmentManager.fragments) {
            if (frag.isVisible) {
                frag.childFragmentManager.let {
                    if (it.backStackEntryCount > 0) {
                        it.popBackStack()
                        return
                    }
                }
            }
        }

        super.onBackPressed()
    }
}