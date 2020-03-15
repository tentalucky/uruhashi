package mahoroba.uruhashi.domain

data class PersonName(
    val familyName: String?,
    val firstName: String?,
    val nameType: NameType
) {
    companion object {
        fun getEmpty() = PersonName(null, null, NameType.FAMILY_NAME_FIRST)
    }

    val fullName: String
        get() {
            return if (nameType == NameType.FAMILY_NAME_FIRST) {
                "${familyName ?: ""} ${firstName ?: ""}"
            } else {
                "${firstName ?: ""} ${familyName ?: ""}"
            }
        }
}