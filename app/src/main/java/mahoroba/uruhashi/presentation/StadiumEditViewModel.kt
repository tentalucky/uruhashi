package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.*
import android.os.Bundle
import android.view.View
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.data.StadiumRepository
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.usecase.StadiumManagementUseCase
import kotlin.concurrent.thread

class StadiumEditViewModel(application: Application, private val stadiumId: ID?) : AndroidViewModel(application) {

    class Factory(private val application: Application, private val bundle: Bundle?)
        : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return StadiumEditViewModel(application, bundle?.getSerializable("id") as ID?) as T
        }
    }

    private val useCase = StadiumManagementUseCase(StadiumRepository(application))

    private val mIsLoaded = MutableLiveData<Boolean>()
    val isLoaded: LiveData<Boolean>
        get() = mIsLoaded

    private val mId = MutableLiveData<String>()
    val id: LiveData<String>
        get() = mId

    val name: MutableLiveData<String> = MutableLiveData()
    val abbreviatedName: MutableLiveData<String> = MutableLiveData()
    val priority: MutableLiveData<Float> = MutableLiveData()

    val closeActivity = LiveEvent<Unit>()

    init {
        mIsLoaded.value = false
        thread {
            if (stadiumId != null) {
                val stadium = useCase.findStadium(stadiumId)
                mId.postValue(stadium.id.value)
                name.postValue(stadium.name)
                abbreviatedName.postValue(stadium.abbreviatedName)
                priority.postValue(stadium.priority.toFloat())
            } else {
                mId.postValue("")
                name.postValue("")
                abbreviatedName.postValue("")
                priority.postValue(0f)
            }

            mIsLoaded.postValue(true)
        }
    }

    fun onRegisterButtonClicked(v: View) {
        val data = StadiumManagementUseCase.InputData(
            if (stadiumId == null) ID() else ID(id.value!!),
            name.value,
            abbreviatedName.value,
            priority.value?.toInt()
        )

        useCase.saveStadium(data)
        closeActivity.call(Unit)
    }
}