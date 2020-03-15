package mahoroba.uruhashi.presentation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_player_list.*
import mahoroba.uruhashi.R
import mahoroba.uruhashi.databinding.ActivityPlayerListBinding

class PlayerListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var twoPane: Boolean = false
    private lateinit var viewModel: PlayerListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PlayerListViewModel::class.java)

        val binding = DataBindingUtil.setContentView<ActivityPlayerListBinding>(this, R.layout.activity_player_list)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.player, R.string.player).let {
            binding.drawerLayout?.addDrawerListener(it)
            it.syncState()
        }

        navigationView?.setNavigationItemSelectedListener(this)

        twoPane = (detail != null)

        if (twoPane) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.list, PlayerListFragment.newInstance())
                .replace(R.id.detail, PlayerDetailFragment.newInstance())
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.list, PlayerListFragment.newInstance())
                .commit()
        }

        registerObservers()
    }

    private fun registerObservers() {
        viewModel.navigating.observe(this, "tag", Observer { it?.navigate(this) })
        viewModel.activityMethod.observe(this, "tag", Observer { it?.invoke(this) })

        if (!twoPane) {
            viewModel.onSelected.observe(this, "tag", Observer {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.list, PlayerDetailFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.master_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_create -> { viewModel.createCommand(null); true }
        R.id.action_edit -> { viewModel.editCommand(null); true }
        R.id.action_delete -> { viewModel.deleteCommand(null); true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_game -> { viewModel.navigateToGameList() }
            R.id.menu_team -> { viewModel.navigateToTeamList() }
            R.id.menu_player -> { viewModel.navigateToPlayerList() }
            R.id.menu_stadium -> { viewModel.navigateToStadiumList() }
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }
}
