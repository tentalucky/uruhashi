package mahoroba.uruhashi.data.query

import android.app.Application
import android.arch.lifecycle.LiveData
import mahoroba.uruhashi.data.AppDatabaseProvider
import mahoroba.uruhashi.usecase.query.GameSummaryDto
import mahoroba.uruhashi.usecase.query.IGameQueryService

class GameQueryService(application: Application) : IGameQueryService {
    private val db = AppDatabaseProvider.getDatabase(application)
    private val dao: GameQueryServiceDao

    init {
        dao = db.gameQueryDao
    }

    override fun getGameSummaryList(): LiveData<List<GameSummaryDto>> {
        return dao.observeGameSummaryList()
    }
}