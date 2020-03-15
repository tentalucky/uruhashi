package mahoroba.uruhashi.common

open class NonNullLiveData<T>(initialValue: T) : LateInitLiveData<T>() {
    init {
        value = initialValue
    }
}