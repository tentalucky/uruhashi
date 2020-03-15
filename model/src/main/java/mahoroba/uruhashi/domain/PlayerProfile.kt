package mahoroba.uruhashi.domain

class PlayerProfile(
    val id: ID,
    val name: PersonName,
    val bats: HandType?,
    val throws: HandType?
) {
    val belongings = PlayerBelongings(this)
}