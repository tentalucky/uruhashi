package mahoroba.uruhashi.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.TeamProfile

@Entity(tableName = "teamProfile")
class TeamProfileData(
    @PrimaryKey val id: String,
    var name: String?,
    var abbreviatedName: String?,
    var priority: Int) {

    constructor(teamProfile: TeamProfile) : this(teamProfile.id.value, teamProfile.name, teamProfile.abbreviatedName, teamProfile.priority)

    fun toTeamProfile() : TeamProfile {
        return TeamProfile(ID(id), name, abbreviatedName, priority)
    }

}