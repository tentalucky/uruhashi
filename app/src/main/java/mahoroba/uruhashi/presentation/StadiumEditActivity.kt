package mahoroba.uruhashi.presentation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_stadium_list.*
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.ActivityStadiumEditBinding

class StadiumEditActivity : AppCompatActivity() {
    private lateinit var viewModel: StadiumEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders
            .of(this, StadiumEditViewModel.Factory(application, intent.getBundleExtra("param")))
            .get(StadiumEditViewModel::class.java)

        val binding = DataBindingUtil
            .setContentView<ActivityStadiumEditBinding>(this, R.layout.activity_stadium_edit)
        binding.setLifecycleOwner(this)
        binding.vm = viewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.closeActivity.observe(this, Observer { finish() })

        supportFragmentManager.beginTransaction()
            .replace(R.id.content, StadiumEditFragment.newInstance())
            .commit()
    }
}
