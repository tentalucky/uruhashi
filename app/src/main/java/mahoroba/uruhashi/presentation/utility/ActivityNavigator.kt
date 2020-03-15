package mahoroba.uruhashi.presentation.utility

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.IntentCompat
import mahoroba.uruhashi.presentation.*

class ActivityNavigator(private val destination: ActivityType, private val bundle: Bundle? = null) {
    fun navigate(activity: Activity) {
        val intent = Intent(activity, destination.cls)
        if (destination.isTopLevel) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        bundle?.let { intent.putExtra("param", bundle) }
        activity.startActivity(intent)
    }

    enum class ActivityType(val cls: Class<*>, val isTopLevel: Boolean) {
        STADIUM_LIST(StadiumListActivity::class.java, true),
        STADIUM_EDIT(StadiumEditActivity::class.java, false),
        TEAM_LIST(TeamListActivity::class.java, true),
        TEAM_EDIT(TeamEditActivity::class.java, false),
        TEAM_MEMBER_EDIT(TeamMemberEditActivity::class.java, false),
        PLAYER_LIST(PlayerListActivity::class.java, true),
        PLAYER_EDIT(PlayerEditActivity::class.java, false),
        GAME_LIST(GameListActivity::class.java, true),
        SCORE_KEEP(ScoreKeepingActivity::class.java, false)
    }
}