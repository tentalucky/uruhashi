package mahoroba.uruhashi.presentation

import mahoroba.uruhashi.presentation.PlayInputViewModel.*
import mahoroba.uruhashi.presentation.base.ScreenSuite
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class EasyReferenceSuite(
    onCompleteListener: () -> Unit,
    onCancelListener: () -> Unit,
    activeFragmentSetter: (ActiveFragmentType, ScreenSuite) -> Unit,
    private val scoreBoardViewModel: ScoreBoardViewModel
) : ScreenSuite(onCompleteListener, onCancelListener, activeFragmentSetter) {

    override val activeFragmentType: ActiveFragmentType
        get() = ActiveFragmentType.EASY_REFERENCE

    private val onBackListener = object: ScoreBoardViewModel.OnBackListener {
        override fun onBack() {
            back()
        }
    }

    fun back() {
        cancel()
        scoreBoardViewModel.removeOnBackListener(onBackListener)
    }

    init {
        scoreBoardViewModel.registerOnBackListener(onBackListener)
    }
}