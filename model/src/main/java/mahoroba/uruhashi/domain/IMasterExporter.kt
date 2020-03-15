package mahoroba.uruhashi.domain

interface IMasterExporter {
    fun export(stadiums: List<Stadium>, teams: List<TeamProfile>, players: List<PlayerProfile>)
}