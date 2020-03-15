package mahoroba.uruhashi.presentation.spinnersource

import android.app.Application
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.HandType

object HandTypeListPresenter {

    fun getList(application: Application) : Array<String>
        = application.resources.getStringArray(R.array.hand_type)

    fun getIndex(value: HandType?) = when (value) {
        null -> 0
        HandType.RIGHT -> 1
        HandType.LEFT -> 2
        HandType.BOTH -> 3
    }

    fun getValue(index: Int) = when (index) {
        0 -> null
        1 -> HandType.RIGHT
        2 -> HandType.LEFT
        3 -> HandType.BOTH
        else -> throw IndexOutOfBoundsException()
    }
}