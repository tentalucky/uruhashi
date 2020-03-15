package mahoroba.uruhashi.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.ITeamRepository
import mahoroba.uruhashi.domain.TeamProfile
import kotlin.concurrent.thread

class TeamRepository(application: Application) : ITeamRepository {
    private val db: AppDatabase = AppDatabaseProvider.getDatabase(application)
    private val teamDao: TeamDao
    private val registerDao: RegisterDao

    init {
        teamDao = db.teamDao
        registerDao = db.registerDao
    }

    override fun save(teamProfile: TeamProfile) {
        db.runInTransaction {
            if (teamProfile.id.isJustGenerated) {
                teamDao.insertTeam(TeamProfileData(teamProfile))
                registerDao.insertRegisters(ArrayList<RegisterData>().apply {
                    teamProfile.roster.forEach { this.add(RegisterData(it)) }
                })
            } else {
                teamDao.updateTeam(TeamProfileData(teamProfile))
                registerDao.deleteRegisterWithTeamId(teamProfile.id.value)
                registerDao.insertRegisters(ArrayList<RegisterData>().apply {
                    teamProfile.roster.forEach { this.add(RegisterData(it)) }
                })
            }
        }
    }

    override fun delete(teamProfile: TeamProfile) {
        thread {
            db.runInTransaction {
                registerDao.deleteRegisterWithTeamId(teamProfile.id.value)
                teamDao.deleteTeam(TeamProfileData(teamProfile))
            }
        }
    }

    override fun get(teamId: ID): TeamProfile {
        val team = teamDao.findById(teamId.value).toTeamProfile()
        registerDao.findRegisterWithTeamId(teamId.value).forEach {
            team.roster.addRegister(it.toRegister())
        }
        return team
    }

    override fun observeAll(): LiveData<List<TeamProfile>> {
        return Transformations.map(teamDao.observeAll()) { list ->
            val newList = ArrayList<TeamProfile>()
            list.forEach { newList.add(it.toTeamProfile()) }
            newList
        }
    }

    override fun findAll(): List<TeamProfile> {
        val newList = ArrayList<TeamProfile>()
        teamDao.findAll().forEach {
            newList.add(it.toTeamProfile())
        }
        return newList
    }
}