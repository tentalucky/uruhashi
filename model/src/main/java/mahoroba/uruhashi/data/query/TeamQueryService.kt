package mahoroba.uruhashi.data.query

import android.app.Application
import mahoroba.uruhashi.data.AppDatabase
import mahoroba.uruhashi.data.AppDatabaseProvider
import mahoroba.uruhashi.domain.TeamProfile
import mahoroba.uruhashi.usecase.query.ITeamQueryService
import mahoroba.uruhashi.usecase.query.TeamMemberInfoDto

class TeamQueryService(application: Application) : ITeamQueryService {
    private val db: AppDatabase = AppDatabaseProvider.getDatabase(application)
    private val dao: TeamQueryServiceDao

    init {
        dao = db.teamQueryDao
    }

    override fun getTeamMembersInfo(teamProfile: TeamProfile): List<TeamMemberInfoDto> {
        return dao.getTeamMemberInfo(teamProfile.id.value).sortedBy { i ->
            if (i.uniformNumber == null) {
                return@sortedBy Int.MAX_VALUE
            }

            var isDigit = true
            i.uniformNumber?.forEach {
                isDigit = (isDigit and it.isDigit())
            }

            if (isDigit)i.uniformNumber.toInt() else Int.MAX_VALUE
        }
    }
}