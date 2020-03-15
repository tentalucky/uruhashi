package mahoroba.uruhashi.common

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread

open class LateInitLiveData<T> : LiveData<T>() {

    private val observers = mutableMapOf<NonNullObserver<T>, Observer<T>>()

    @MainThread
    @Deprecated(
        message = "use observe for NonNull.",
        replaceWith = ReplaceWith("observe(owner, NonNullObserver)"),
        level = DeprecationLevel.HIDDEN
    )
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        super.observe(owner, observer)
    }

    @MainThread
    @Deprecated(
        message = "use observeForever for NonNull.",
        replaceWith = ReplaceWith("observeForever(owner, NonNollObserver)"),
        level = DeprecationLevel.HIDDEN
    )
    override fun observeForever(observer: Observer<T>) {
        super.observeForever(observer)
    }

    @MainThread
    @Deprecated(
        message = "use removeObserver for NoNull.",
        replaceWith = ReplaceWith("removeObserver(owner, NonNullObserver)"),
        level = DeprecationLevel.HIDDEN
    )
    override fun removeObserver(observer: Observer<T>) {
        super.removeObserver(observer)
    }

    @MainThread
    open fun observe(owner: LifecycleOwner, nonNullObserver: NonNullObserver<T>) {
        val observer = Observer<T> { nonNullObserver.onChanged(it!!) }
        observers[nonNullObserver] = observer
        super.observe(owner, observer)
    }

    @MainThread
    open fun observeForever(nonNullObserver: NonNullObserver<T>) {
        val observer = Observer<T> { nonNullObserver.onChanged(it!!) }
        observers[nonNullObserver] = observer
        super.observeForever(observer)
    }

    @MainThread
    open fun removeObserver(nonNullObserver: NonNullObserver<T>) {
        val observer = observers[nonNullObserver]
        if (observer != null) {
            observers.remove(nonNullObserver)
            super.removeObserver(observer)
        }
    }

    override fun getValue(): T {
        return super.getValue() ?: throw UninitializedPropertyAccessException()
    }

    @MainThread
    public override fun setValue(value: T) {
        super.setValue(value)
    }

    public override fun postValue(value: T) {
        super.postValue(value)
    }
}