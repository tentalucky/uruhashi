package mahoroba.uruhashi.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.IPlayerRepository
import mahoroba.uruhashi.domain.PlayerProfile
import kotlin.concurrent.thread

class PlayerRepository(application: Application) : IPlayerRepository {
    private val db: AppDatabase = AppDatabaseProvider.getDatabase(application)
    private val playerDao: PlayerDao
    private val registerDao: RegisterDao

    init {
        playerDao = db.playerDao
        registerDao = db.registerDao
    }

    override fun save(playerProfile: PlayerProfile) {
        thread {
            db.runInTransaction {
                if (playerProfile.id.isJustGenerated) {
                    playerDao.insertPlayer(PlayerData(playerProfile))
                    registerDao.insertRegisters(ArrayList<RegisterData>().apply {
                        playerProfile.belongings.forEach { this.add(RegisterData(it)) }
                    })
                } else {
                    playerDao.updatePlayer(PlayerData(playerProfile))
                    registerDao.deleteRegisterWithPlayerId(playerProfile.id.value)
                    registerDao.insertRegisters(ArrayList<RegisterData>().apply {
                        playerProfile.belongings.forEach { this.add(RegisterData(it)) }
                    })
                }
            }
        }
    }

    override fun delete(playerProfile: PlayerProfile) {
        thread {
            db.runInTransaction {
                registerDao.deleteRegisterWithPlayerId(playerProfile.id.value)
                playerDao.deletePlayer(PlayerData(playerProfile))
            }
        }
    }

    override fun get(playerId: ID): PlayerProfile {
        val player = playerDao.findById(playerId.value).toPlayer()
        registerDao.findRegisterWithPlayerId(playerId.value).forEach {
            player.belongings.addRegister(it.toRegister())
        }
        return player
    }

    override fun observeAll(): LiveData<List<PlayerProfile>> {
        return Transformations.map(playerDao.observeAll()) { list ->
            val newList = ArrayList<PlayerProfile>()
            list.forEach { newList.add(it.toPlayer()) }
            newList
        }
    }
}