package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.ID

interface IGameRepository {
    fun save(game: Game)
    fun delete(game: Game)
    fun get(gameId: ID) : Game
}