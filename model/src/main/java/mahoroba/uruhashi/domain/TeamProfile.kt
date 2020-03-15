package mahoroba.uruhashi.domain

class TeamProfile(
    val id: ID,
    var name: String?,
    var abbreviatedName: String?,
    var priority: Int
) {
    val roster = TeamsRoster(this)
}