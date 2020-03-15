package mahoroba.uruhashi.data.query

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import mahoroba.uruhashi.usecase.query.GameSummaryDto

@Dao
interface GameQueryServiceDao {

    @Query("""
        SELECT
          game.id AS gameId,
          game.gameName AS gameName,
          game.date As gameDate,
          game.homeTeamName AS homeTeamName,
          game.homeTeamAbbreviatedName AS homeTeamAbbreviatedName,
          game.visitorTeamName AS VisitorTeamName,
          game.visitorTeamAbbreviatedName AS visitorTeamAbbreviatedName,
          game.stadiumName AS stadiumName,
          game.stadiumAbbreviatedName AS stadiumAbbreviatedName
        FROM game
    """)
    fun observeGameSummaryList() : LiveData<List<GameSummaryDto>>
}