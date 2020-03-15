package mahoroba.uruhashi.usecase.query

data class PlayerBelongingInfoDto(
    val teamId: String,
    val teamName: String?,
    val uniformNumber: String?
)