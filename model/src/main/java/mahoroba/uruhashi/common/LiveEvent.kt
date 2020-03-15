package mahoroba.uruhashi.common

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread

open class LiveEvent<T> : LiveData<T>() {

    private val dispatchedTagSet = mutableSetOf<String>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, "", observer)
    }

    @MainThread
    override fun observeForever(observer: Observer<T>) {
        super.observeForever(observer)
    }

    @MainThread
    open fun observe(owner: LifecycleOwner, tag: String, observer: Observer<T>) {
        super.observe(owner, Observer {
            val internalTag = owner::class.java.name + "#" + tag
            if (!dispatchedTagSet.contains(internalTag)) {
                dispatchedTagSet.add(internalTag)
                observer.onChanged(it)
            }
        })
    }

    @MainThread
    open fun observeForever(tag: String, observer: Observer<T>) {
        super.observeForever {
            if (!dispatchedTagSet.contains(tag)) {
                dispatchedTagSet.add(tag)
                observer.onChanged(it)
            }
        }
    }

    @MainThread
    open fun call(t: T?) {
        dispatchedTagSet.clear()
        value = t
    }

    open fun postCall(t: T?) {
        dispatchedTagSet.clear()
        postValue(t)
    }
}