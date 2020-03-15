package mahoroba.uruhashi.presentation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_team_edit.*
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.ActivityTeamMemberEditBinding
import mahoroba.uruhashi.presentation.base.BaseActivity

class TeamMemberEditActivity : BaseActivity() {
    lateinit var mViewModel: TeamMemberEditViewModel
    override val viewModel: TeamMemberEditViewModel
        get() = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProviders
            .of(this, TeamMemberEditViewModel.Factory(application, intent.getBundleExtra("param")))
            .get(TeamMemberEditViewModel::class.java)

        val binding = DataBindingUtil
            .setContentView<ActivityTeamMemberEditBinding>(this, R.layout.activity_team_member_edit)
        binding.setLifecycleOwner(this)
        binding.vm = viewModel

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.content, TeamMemberEditFragment.newInstance())
            .commit()

        viewModel.activityMethod.observe(this, "tag", Observer { it?.invoke(this) })
    }
}
