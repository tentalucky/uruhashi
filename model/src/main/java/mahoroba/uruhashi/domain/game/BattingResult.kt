package mahoroba.uruhashi.domain.game

enum class BattingResult(val isBaseHit: Boolean) {
    SINGLE(true),
    DOUBLE(true),
    TRIPLE(true),
    HOME_RUN(true),
    GROUND_OUT(false),
    LINE_OUT(false),
    FLY_OUT(false),
    FOUL_LINE_OUT(false),
    FOUL_FLY_OUT(false),
    STRIKEOUT(false),
    SACRIFICE_FLY(false),
    SACRIFICE_HIT(false),
    WALK(false),
    INTENTIONAL_WALK(false),
    HIT_BY_PITCH(false),
    INTERFERE(false),
    OBSTRUCTION(false),
    INTERRUPT(false),
    NO_ENTRY(false)
}