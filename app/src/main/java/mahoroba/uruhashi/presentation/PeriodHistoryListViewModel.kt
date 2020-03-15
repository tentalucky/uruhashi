package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import android.view.View
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class PeriodHistoryListViewModel(
    application: Application,
    val gameBaseInfo: LiveData<ScoreKeepingUseCase.GameBaseInfo>,
    val boxScore: LiveData<ScoreKeepingUseCase.BoxScoreDto>,
    val parentViewModel: PlayInputViewModel
) : BaseViewModel(application) {

    class PeriodHistoryListItemViewModel(
        val gameBaseInfo: ScoreKeepingUseCase.GameBaseInfo,
        val period: ScoreKeepingUseCase.PeriodDto,
        val parentViewModel: PlayInputViewModel
    ) {
        val requireShowPopupMenu = LiveEvent<Unit>()

        fun onLongClicked(v: View): Boolean {
            requireShowPopupMenu.call(Unit)
            return true
        }

        fun insertOffenceSubstitution() {
            parentViewModel.openInsertOffenceSubstitution(period)
        }

        fun insertDefenceSubstitution() {
            parentViewModel.openInsertDefenceSubstitution(period)
        }

        fun modifyThisSubstitution() {
            if (period is ScoreKeepingUseCase.SubstitutionDto) {
                parentViewModel.openModifySubstitution(period)
            }
        }

        fun deleteThisSubstitution() {
            if (period is ScoreKeepingUseCase.SubstitutionDto) {
                parentViewModel.openDeleteSubstitution(period)
            }
        }
    }

    val itemCount: LiveData<Int> = Transformations.map(boxScore) {
        it.innings.sumBy { inn ->
            inn.plateAppearances.sumBy { pa ->
                pa.periods.count()
            }
        }
    }

    fun getItemAt(index: Int): PeriodHistoryListItemViewModel {
        var i = 0
        boxScore.value?.innings?.asReversed()?.forEach { inn ->
            inn.plateAppearances.asReversed().forEach { pa ->
                pa.periods.asReversed().forEach { p ->
                    if (i == index) {
                        return PeriodHistoryListItemViewModel(
                            gameBaseInfo.value!!, p, parentViewModel
                        )
                    }
                    i++
                }
            }
        }
        throw IndexOutOfBoundsException()
    }
}