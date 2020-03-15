package mahoroba.uruhashi.presentation.enumStringConverter

import android.app.Application
import android.content.res.Resources
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.game.Position

class PositionConverter(private val application: Application) {
    fun toString(p: Position): String = when (p) {
            Position.PITCHER -> application.getString(R.string.display_position_pitcher)
            Position.CATCHER -> application.getString(R.string.display_position_catcher)
            Position.FIRST_BASEMAN -> application.getString(R.string.display_position_first_baseman)
            Position.SECOND_BASEMAN -> application.getString(R.string.display_position_second_baseman)
            Position.THIRD_BASEMAN -> application.getString(R.string.display_position_third_baseman)
            Position.SHORT_STOP -> application.getString(R.string.display_position_shortstop)
            Position.LEFT_FIELDER -> application.getString(R.string.display_position_left_fielder)
            Position.CENTER_FIELDER -> application.getString(R.string.display_position_center_fielder)
            Position.RIGHT_FIELDER -> application.getString(R.string.display_position_right_fielder)
            Position.DESIGNATED_HITTER -> application.getString(R.string.display_position_designated_hitter)
            Position.PINCH_HITTER -> application.getString(R.string.display_position_pinch_hitter)
            Position.PINCH_RUNNER -> application.getString(R.string.display_position_pinch_runner)
            Position.NO_ENTRY -> "-"
        }
}