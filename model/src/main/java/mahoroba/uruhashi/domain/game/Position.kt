package mahoroba.uruhashi.domain.game

enum class Position(
    val isFieldPosition: Boolean,
    val isStaticPosition: Boolean,
    val singleCharacter: String,
    val abbreviated: String
) {
    PITCHER(true, true, "1", "P"),
    CATCHER(true, true, "2", "C"),
    FIRST_BASEMAN(true, true, "3", "1B"),
    SECOND_BASEMAN(true, true, "4", "2B"),
    THIRD_BASEMAN(true, true, "5", "3B"),
    SHORT_STOP(true, true, "6", "SS"),
    LEFT_FIELDER(true, true, "7", "LF"),
    CENTER_FIELDER(true, true, "8", "CF"),
    RIGHT_FIELDER(true, true, "9", "RF"),
    DESIGNATED_HITTER(false, true, "D", "DH"),
    PINCH_HITTER(false, false, "H", "PH"),
    PINCH_RUNNER(false, false, "R", "PR"),
    NO_ENTRY(false, false, "", "");

}