package mahoroba.uruhashi.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.IStadiumRepository
import mahoroba.uruhashi.domain.Stadium
import kotlin.concurrent.thread

class StadiumRepository(application: Application) : IStadiumRepository {
    private val stadiumDao : StadiumDao

    init {
        val db = AppDatabaseProvider.getDatabase(application)
        stadiumDao = db.stadiumDao
    }

    override fun save(stadium: Stadium) {
        thread {
            if (stadium.id.isJustGenerated) {
                stadiumDao.insert(StadiumData(stadium))
            } else {
                stadiumDao.update(StadiumData(stadium))
            }
        }
    }

    override fun delete(id: ID) {
        thread {
            stadiumDao.delete(stadiumDao.findById(id.value))
        }
    }

    override fun get(id: ID): Stadium {
        return stadiumDao.findById(id.value).toStadium()
    }

    override fun observeAll(): LiveData<List<Stadium>> {
        return Transformations.map(stadiumDao.observeAll()) { source ->
            val newList = ArrayList<Stadium>()
            source?.forEach { newList.add(it.toStadium()) }
            newList
        }
    }
}