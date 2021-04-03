package mahoroba.uruhashi.presentation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.ActivityScoreKeepingBinding
import mahoroba.uruhashi.presentation.base.BaseActivity

class ScoreKeepingActivity : BaseActivity() {
    private lateinit var mViewModel: ScoreKeepingViewModel
    override val viewModel: ScoreKeepingViewModel
        get() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProviders
            .of(this, ScoreKeepingViewModel.Factory(application, intent.getBundleExtra("param")))
            .get(ScoreKeepingViewModel::class.java)

        val binding = DataBindingUtil
            .setContentView<ActivityScoreKeepingBinding>(this, R.layout.activity_score_keeping)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        viewModel.activeFragmentType.observe(this, Observer { t -> switchFragment(t!!) })
        viewModel.gameLoadedEvent.observe(this, Observer { t -> viewModel.onGameLoaded() })
    }

    private fun switchFragment(type : ScoreKeepingViewModel.ActiveFragmentType) {
        when (type) {
            ScoreKeepingViewModel.ActiveFragmentType.GAME_INFO_INPUT -> GameInfoInputFragment.newInstance()
            ScoreKeepingViewModel.ActiveFragmentType.BOX_SCORE_INPUT -> PlayInputFragment.newInstance()
            ScoreKeepingViewModel.ActiveFragmentType.GAME_ENDING -> GameEndingFragment.newInstance()
        }.let { t ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.content, t)
                .commit()
        }
    }
}
