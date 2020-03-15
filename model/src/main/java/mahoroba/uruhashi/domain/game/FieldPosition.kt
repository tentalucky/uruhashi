package mahoroba.uruhashi.domain.game

enum class FieldPosition(val singleCharacter: String) {
    PITCHER("1"),
    CATCHER("2"),
    FIRST_BASEMAN("3"),
    SECOND_BASEMAN("4"),
    THIRD_BASEMAN("5"),
    SHORT_STOP("6"),
    LEFT_FIELDER("7"),
    CENTER_FIELDER("8"),
    RIGHT_FIELDER("9"),
}