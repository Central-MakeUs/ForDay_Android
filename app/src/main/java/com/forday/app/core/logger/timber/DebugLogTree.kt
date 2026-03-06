package com.forday.app.core.logger.timber

import timber.log.Timber

class DebugLogTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String = TAG

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val threadName = Thread.currentThread().name
        super.log(priority, tag, "[$threadName] $message", t)
    }

    companion object {
        private const val TAG = "Forday"
    }
}