package mahoroba.uruhashi.presentation

import mahoroba.uruhashi.presentation.base.ScreenSuite
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase.*

class OffenceSubstitutionInputSuite(
    private val parentViewModel: PlayInputViewModel,
    private val useCase: ScoreKeepingUseCase,
    val mode: Mode,
    val selectedPeriod: PeriodDto?,
    onCompleteListener: () -> Unit,
    onCancelListener: () -> Unit,
    activeFragmentSetter: (PlayInputViewModel.ActiveFragmentType, ScreenSuite) -> Unit
) : ScreenSuite(onCompleteListener, onCancelListener, activeFragmentSetter) {

    enum class Mode {
        NEW,
        INSERT,
        MODIFY,
        DELETE
    }

    override val activeFragmentType: PlayInputViewModel.ActiveFragmentType
        get() = PlayInputViewModel.ActiveFragmentType.OFFENCE_SUBSTITUTION

    private var mOffenceSubstitutionInputViewModel: OffenceSubstitutionInputViewModel? = null
    val offenceSubstitutionInputViewModel: OffenceSubstitutionInputViewModel
        get() {
            if (mOffenceSubstitutionInputViewModel == null) mOffenceSubstitutionInputViewModel =
                OffenceSubstitutionInputViewModel(parentViewModel.getApplication(), this)
            return mOffenceSubstitutionInputViewModel!!
        }

    val gameState = useCase.latestGameState
    val gameBaseInfo = useCase.gameBaseInfo

    fun settleOffenceTeamSubstitution() {
        val pinchHitter = mOffenceSubstitutionInputViewModel?.pinchHitter?.let {
            PlayerInfoDto(it.id, it.name, it.bats, it.throws, it.uniformNumber)
        }
        val pinchRunner1 = mOffenceSubstitutionInputViewModel?.pinchRunner1?.let {
            PlayerInfoDto(it.id, it.name, it.bats, it.throws, it.uniformNumber)
        }
        val pinchRunner2 = mOffenceSubstitutionInputViewModel?.pinchRunner2?.let {
            PlayerInfoDto(it.id, it.name, it.bats, it.throws, it.uniformNumber)
        }
        val pinchRunner3 = mOffenceSubstitutionInputViewModel?.pinchRunner3?.let {
            PlayerInfoDto(it.id, it.name, it.bats, it.throws, it.uniformNumber)
        }

        when (mode) {
            Mode.NEW ->
                useCase.addOffenceSubstitution(
                    pinchHitter, pinchRunner1, pinchRunner2, pinchRunner3
                )
            Mode.INSERT ->
                useCase.insertOffenceSubstitution(
                    selectedPeriod!!, pinchHitter, pinchRunner1, pinchRunner2, pinchRunner3
                )
            Mode.MODIFY ->
                useCase.modifyOffenceSubstitution(
                    selectedPeriod!! as SubstitutionDto,
                    pinchHitter, pinchRunner1, pinchRunner2, pinchRunner3
                )
        }
        complete()
    }

    fun back() {
        cancel()
    }

    fun load(substitutionDto: SubstitutionDto) {
        mOffenceSubstitutionInputViewModel?.reproduceInput(substitutionDto)
    }
}