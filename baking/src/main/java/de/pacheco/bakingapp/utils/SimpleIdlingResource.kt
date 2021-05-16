package de.pacheco.bakingapp.utils

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean

class SimpleIdlingResource : IdlingResource {
    var isIdle = AtomicBoolean(true)

    @Volatile
    private var callback: IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return javaClass.name
    }

    override fun isIdleNow(): Boolean {
        return isIdle.get()
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        this.callback = callback
    }

    fun setIdleState(isIdleNow: Boolean) {
        isIdle.set(isIdleNow)
        if (callback != null && isIdleNow) {
            callback!!.onTransitionToIdle()
        }
    }
}