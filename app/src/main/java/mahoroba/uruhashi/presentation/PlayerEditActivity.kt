package mahoroba.uruhashi.presentation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_team_edit.*
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.ActivityPlayerEditBinding

class PlayerEditActivity : AppCompatActivity() {
    private lateinit var viewModel: PlayerEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders
            .of(this, PlayerEditViewModel.Factory(application, intent.getBundleExtra("param")))
            .get(PlayerEditViewModel::class.java)

        val binding = DataBindingUtil
            .setContentView<ActivityPlayerEditBinding>(this, R.layout.activity_player_edit)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.closingActivity.observe(this, Observer { finish() })

        supportFragmentManager.beginTransaction()
            .replace(R.id.content, PlayerEditFragment.newInstance())
            .commit()
    }
}
