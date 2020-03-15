package mahoroba.uruhashi.presentation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_team_edit.*
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.ActivityTeamEditBinding

class TeamEditActivity : AppCompatActivity() {
    private lateinit var viewModel: TeamEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders
            .of(this, TeamEditViewModel.Factory(application, intent.getBundleExtra("param")))
            .get(TeamEditViewModel::class.java)

        val binding = DataBindingUtil
            .setContentView<ActivityTeamEditBinding>(this, R.layout.activity_team_edit)
        binding.setLifecycleOwner(this)
        binding.vm = viewModel

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.activityMethod.observe(this, Observer { it -> it?.invoke(this) })

        supportFragmentManager.beginTransaction()
            .replace(R.id.content, TeamEditFragment.newInstance())
            .commit()
    }
}
