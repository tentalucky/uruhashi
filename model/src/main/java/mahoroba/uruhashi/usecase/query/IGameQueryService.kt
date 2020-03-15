package mahoroba.uruhashi.usecase.query

import android.arch.lifecycle.LiveData
import mahoroba.uruhashi.usecase.GameManagementUseCase

interface IGameQueryService {
    fun getGameSummaryList() : LiveData<List<GameSummaryDto>>
}