package mahoroba.uruhashi.presentation.spinnersource

import android.app.Application
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.NameType

object NameTypeListPresenter {

    fun getList(application: Application) : Array<String>
            = application.resources.getStringArray(R.array.name_type)

    fun getIndex(value: NameType) = when (value) {
        NameType.FAMILY_NAME_FIRST -> 0
        NameType.FIRST_NAME_FIRST -> 1
    }

    fun getValue(index: Int) = when (index) {
        0 -> NameType.FAMILY_NAME_FIRST
        1 -> NameType.FIRST_NAME_FIRST
        else -> throw IndexOutOfBoundsException()
    }
}