package mahoroba.uruhashi.domain.game

enum class RunnerState {
    ON_FIRST_BASE(false),
    ON_SECOND_BASE(false),
    ON_THIRD_BASE(false),
    HOME_IN(false),
    OUT_IN_PROGRESSING(true),
    OUT_IN_RETURNING(true),
    FLOATING(false);

    val isOut: Boolean
    val hasCompleted: Boolean
        get() = isOut || this == HOME_IN

    constructor(isOut: Boolean) {
        this.isOut = isOut
    }
}