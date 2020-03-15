package mahoroba.uruhashi.common

import java.util.*

val Calendar.year: Int
    get() = this.get(Calendar.YEAR)
val Calendar.month: Int
    get() = this.get(Calendar.MONTH)
val Calendar.dayOfMonth: Int
    get() = this.get(Calendar.DAY_OF_MONTH)
val Calendar.hourOfDay: Int
    get() = this.get(Calendar.HOUR_OF_DAY)
val Calendar.minute: Int
    get() = this.get(Calendar.MINUTE)