package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.secondary.Lineup
import mahoroba.uruhashi.domain.game.secondary.Situation

class Substitution(
    previousSituation: Situation,
    val teamClass: TeamClass,
    val cancelDH: Boolean,
    positionChangingList: List<PositionChanging>,
    playerChangingList: List<PlayerChanging>,
    homeLineup: Lineup,
    visitorLineup: Lineup
) : Period(previousSituation, homeLineup, visitorLineup) {
    private val mPositionChangingList: ArrayList<PositionChanging> = ArrayList()
    val positionChangingList: List<PositionChanging>
        get() = mPositionChangingList

    private val mPlayerChangingList: ArrayList<PlayerChanging> = ArrayList()
    val playerChangingList: List<PlayerChanging>
        get() = mPlayerChangingList

    init {
        this.mPositionChangingList.addAll(positionChangingList)
        this.mPlayerChangingList.addAll(playerChangingList)
    }
}