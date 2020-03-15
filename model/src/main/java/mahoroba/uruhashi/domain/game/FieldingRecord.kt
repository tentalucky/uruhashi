package mahoroba.uruhashi.domain.game

enum class FieldingRecord(val preventMakingOut: Boolean, val isError: Boolean) {
    ERROR_CATCHING(true, true),
    ERROR_THROWING(true, true),
    FIELDERS_CHOICE(true, false),
    TAGGED_OUT(false, false)
}