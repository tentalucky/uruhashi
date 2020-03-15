package mahoroba.uruhashi.usecase

import android.util.Log
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.Game
import mahoroba.uruhashi.domain.game.IGameExporter
import mahoroba.uruhashi.domain.game.IGameRepository
import mahoroba.uruhashi.usecase.query.IGameQueryService

class GameManagementUseCase(
    private val gameRepository: IGameRepository,
    private val gameQueryService: IGameQueryService,
    private val gameExporter: IGameExporter) {

    val gameSummaryList = gameQueryService.getGameSummaryList()

    fun findGame(gameId: ID): Game {
        return gameRepository.get(gameId)
    }

    fun deleteGame(gameId: ID) {
        gameRepository.delete(gameRepository.get(gameId))
    }

    fun exportGame(gameId: ID) {
        val game = gameRepository.get(gameId)
        gameExporter.export(game)
    }
}