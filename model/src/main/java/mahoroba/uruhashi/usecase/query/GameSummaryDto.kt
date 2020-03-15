package mahoroba.uruhashi.usecase.query

import mahoroba.uruhashi.domain.ID
import java.text.SimpleDateFormat
import java.util.*

class GameSummaryDto(
    val gameId: ID,
    val gameName: String?,
    val gameDate: Date?,
    val homeTeamName: String?,
    val homeTeamAbbreviatedName: String?,
    val visitorTeamName: String?,
    val visitorTeamAbbreviatedName: String?,
    val stadiumName: String?,
    val stadiumAbbreviatedName: String?
) {
    val formatDate: String?
        get() {
            return if (gameDate == null) null else SimpleDateFormat("yyyy/MM/dd").format(gameDate)
        }
}