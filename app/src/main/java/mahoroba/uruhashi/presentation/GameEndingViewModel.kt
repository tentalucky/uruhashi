package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.base.ScreenSuite
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase
import java.util.*

class GameEndingViewModel(
    application: Application,
    private val parentViewModel: ScoreKeepingViewModel,
    private val useCase: ScoreKeepingUseCase
) : BaseViewModel(application) {

    enum class ActiveFragmentType {
        RESULT_INPUT,
        EASY_REFERENCE
    }

    private val transitionStack = Stack<ActiveFragmentType>()

    private val mActiveFragmentType = MutableLiveData<ActiveFragmentType>()
    val activeFragmentType: LiveData<ActiveFragmentType> = mActiveFragmentType

    private var mGameResultInputViewModel: GameResultInputViewModel? = null
    val gameResultInputViewModel: GameResultInputViewModel
        get() {
            if (mGameResultInputViewModel == null) {
                mGameResultInputViewModel = GameResultInputViewModel(getApplication(), parentViewModel, useCase)
            }
            return mGameResultInputViewModel!!
        }

    fun openEasyReferenceView() {
        transitionStack.add(ActiveFragmentType.EASY_REFERENCE)
        mActiveFragmentType.value = transitionStack.peek()
    }

    fun back() {
        transitionStack.pop()
        mActiveFragmentType.value = transitionStack.peek()
    }

    init {
        transitionStack.add(ActiveFragmentType.RESULT_INPUT)
        mActiveFragmentType.value = transitionStack.peek()
    }
}