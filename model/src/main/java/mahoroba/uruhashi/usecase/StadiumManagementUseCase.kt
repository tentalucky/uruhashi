package mahoroba.uruhashi.usecase

import android.arch.lifecycle.LiveData
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.IStadiumRepository
import mahoroba.uruhashi.domain.Stadium

class StadiumManagementUseCase(private val stadiumRepository: IStadiumRepository) {
    val allStadiums: LiveData<List<Stadium>> = stadiumRepository.observeAll()

    fun findStadium(id: ID) : Stadium {
        return stadiumRepository.get(id)
    }

    fun saveStadium(inputData: InputData) {
        val stadium = Stadium(
            inputData.id,
            inputData.name ?: "",
            inputData.abbreviatedName ?: "",
            inputData.priority ?: 1)
        stadiumRepository.save(stadium)
    }

    fun deleteStadium(id: ID) {
        stadiumRepository.delete(id)
    }

    data class InputData(
        val id: ID,
        val name: String?,
        val abbreviatedName: String?,
        val priority: Int?
    )
}