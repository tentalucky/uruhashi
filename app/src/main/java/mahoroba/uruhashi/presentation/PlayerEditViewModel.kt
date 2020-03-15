package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.di.QueryServicePresenter
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.NameType
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.dialog.SelectTeamDialogViewModel
import mahoroba.uruhashi.presentation.spinnersource.HandTypeListPresenter
import mahoroba.uruhashi.presentation.spinnersource.NameTypeListPresenter
import mahoroba.uruhashi.usecase.PlayerManagementUseCase
import kotlin.concurrent.thread

class PlayerEditViewModel(application: Application, private val playerId: ID?) :
    BaseViewModel(application), SelectTeamDialogViewModel.Listener {

    class Factory(private val application: Application, private val bundle: Bundle?) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PlayerEditViewModel(application, bundle?.getSerializable("id") as ID?) as T
        }
    }

    private val useCase = PlayerManagementUseCase(
        RepositoryPresenter.getPlayerRepository(application),
        QueryServicePresenter.getPlayerQueryService(application),
        RepositoryPresenter.getTeamRepository(application)
    )

    private val mIsLoaded = MutableLiveData<Boolean>()
    val isLoaded: LiveData<Boolean>
        get() = mIsLoaded

    private val mPlayerId = MutableLiveData<String>()
    val id: LiveData<String>
        get() = mPlayerId

    val familyName = MutableLiveData<String>()
    val firstName = MutableLiveData<String>()
    val fullName = MediatorLiveData<String>()

    val closingActivity = LiveEvent<Unit>()

    val nameTypeList = NameTypeListPresenter.getList(application)
    val handTypeList = HandTypeListPresenter.getList(application)

    val nameTypePosition = MutableLiveData<Int>()
    private var nameType: NameType
        get() = NameTypeListPresenter.getValue(nameTypePosition.value ?: 0)
        set(value) = nameTypePosition.postValue(NameTypeListPresenter.getIndex(value))

    val throwsPosition = MutableLiveData<Int>()
    private var throws: HandType?
        get() = HandTypeListPresenter.getValue(throwsPosition.value ?: 0)
        set(value) = throwsPosition.postValue(HandTypeListPresenter.getIndex(value))

    val batsPosition = MutableLiveData<Int>()
    private var bats: HandType?
        get() = HandTypeListPresenter.getValue(batsPosition.value ?: 0)
        set(value) = batsPosition.postValue(HandTypeListPresenter.getIndex(value))

    private val mBelongings = MutableLiveData<ArrayList<PlayerBelongingEditItemViewModel>>()
    val belongings =
        Transformations.map(mBelongings) { it as List<PlayerBelongingEditItemViewModel> }!!

    val openSelectTeamDialog = LiveEvent<Unit>()

    init {
        val f = fun(fam: String?, fir: String?, typ: NameType?): String =
            if (typ == null || typ == NameType.FAMILY_NAME_FIRST) "$fam $fir"
            else "$fir $fam"
        fullName.addSource(familyName) { s -> fullName.value = f(s, firstName.value, nameType) }
        fullName.addSource(firstName) { s -> fullName.value = f(familyName.value, s, nameType) }
        fullName.addSource(nameTypePosition) { s ->
            fullName.value = f(familyName.value, firstName.value, nameType)
        }

        mIsLoaded.value = false
        thread {
            if (playerId?.isJustGenerated == false) {
                useCase.findPlayer(playerId).let { player ->
                    mPlayerId.postValue(player.id.value)
                    familyName.postValue(player.name.familyName)
                    firstName.postValue(player.name.firstName)
                    nameType = player.name.nameType
                    throws = player.throws
                    bats = player.bats

                    val belongings = ArrayList<PlayerBelongingEditItemViewModel>().apply {
                        useCase.findPlayerBelongings(player).forEach {
                            this.add(
                                PlayerBelongingEditItemViewModel(
                                    ID(it.teamId), it.teamName, it.uniformNumber,
                                    adding = false,
                                    onValueChanged = { mBelongings.value = mBelongings.value },
                                    deleteThis = { t ->
                                        mBelongings.value!!.remove(t)
                                        mBelongings.value = mBelongings.value
                                    }
                                )
                            )
                        }
                    }
                    mBelongings.postValue(belongings)
                }
            } else {
                mPlayerId.postValue("")
                familyName.postValue("")
                firstName.postValue("")
                nameType = NameType.FAMILY_NAME_FIRST
                throws = null
                bats = null

                mBelongings.postValue(ArrayList())
            }

            mIsLoaded.postValue(true)
        }
    }

    fun addBelongingCommand(v: View?) {
        openSelectTeamDialog.call(Unit)
    }

    fun registerCommand(v: View?) {
        if (playerId == null) {
            useCase.registerNewPlayer(
                familyName.value, firstName.value, nameType, bats, throws,
                mBelongings.value!!.filter { t -> !t.removing }.map {
                    PlayerManagementUseCase.PlayerBelonging(
                        it.teamId, it.uniformNumber.value
                    )
                }
            )
        } else {
            useCase.modifyPlayer(
                playerId, familyName.value, firstName.value, nameType, bats, throws,
                mBelongings.value!!.filter { t -> !t.removing }.map {
                    PlayerManagementUseCase.PlayerBelonging(
                        it.teamId, it.uniformNumber.value
                    )
                }
            )
        }

        closingActivity.call(Unit)
    }

    override fun onSelected(arg: SelectTeamDialogViewModel.ResultArg) {
        val handler = Handler()
        thread {
            val team = useCase.findTeam(arg.teamId)

            if (mBelongings.value?.any { t -> t.teamId == arg.teamId } == true) {
                val message = "${team.name} has already been added."
                handler.post {
                    Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
                }
                return@thread
            }

            mBelongings.value?.add(
                PlayerBelongingEditItemViewModel(
                    team.id, team.name, "",
                    adding = true,
                    onValueChanged = { mBelongings.value = mBelongings.value },
                    deleteThis = { t ->
                        mBelongings.value!!.remove(t)
                        mBelongings.value = mBelongings.value
                    }
                )
            )

            mBelongings.postValue(mBelongings.value)
        }
    }

    class PlayerBelongingEditItemViewModel(
        val teamId: ID,
        val teamName: String?,
        uniformNumber: String?,
        val adding: Boolean,
        private val onValueChanged: () -> Unit,
        private val deleteThis: (PlayerBelongingEditItemViewModel) -> Unit
    ) {
        private val mUniformNumber = MutableLiveData<String?>()
        val uniformNumber: LiveData<String?>
            get() = mUniformNumber

        private val mStatus = MutableLiveData<String>()
        val status: LiveData<String>
            get() = mStatus

        var removing: Boolean = false
            get
            private set(value) {
                field = value
                mStatus.value = getStatus()
            }

        val openUniformNumberInput = LiveEvent<(String) -> Unit>()

        init {
            this.mUniformNumber.postValue(uniformNumber)
            mStatus.postValue(getStatus())
        }

        fun onUniformNumberClicked(v: View) {
            openUniformNumberInput.call { input ->
                mUniformNumber.value = input
                onValueChanged()
            }
        }

        fun onDeleteOrCancelClicked(v: View) {
            if (adding) {
                deleteThis(this)
            } else {
                removing = !removing
                onValueChanged()
            }
        }

        private fun getStatus() = when {
            adding -> "追加"
            removing -> "削除"
            else -> ""
        }
    }
}