package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import mahoroba.uruhashi.presentation.customView.PeriodHistoryItemView
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

object PeriodHistoryItemViewBindingAdapter {
    @BindingAdapter("app:periodDto")
    @JvmStatic
    fun setPeriodDto(view: PeriodHistoryItemView, periodDto: ScoreKeepingUseCase.PeriodDto) {
        view.period = periodDto
    }

    @BindingAdapter("app:gameBaseInfo")
    @JvmStatic
    fun setGameBaseInfo(view: PeriodHistoryItemView, gameBaseInfo: ScoreKeepingUseCase.GameBaseInfo) {
        view.gameBaseInfo = gameBaseInfo
    }
}