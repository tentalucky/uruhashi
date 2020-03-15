package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.RunnerState.*

class RunnersAction(val type: RunnerType, val state: RunnerState) {

    val isOut: Boolean
        get() = state == OUT_IN_PROGRESSING || state == OUT_IN_RETURNING
}