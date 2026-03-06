package com.forday.app.core.logger.timber

import android.util.Log
import com.forday.app.core.logger.crashlytics.CrashlyticsManager
import timber.log.Timber

class CrashlyticsTree(
    private val crashlyticsManager: CrashlyticsManager,
) : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        when (priority) {
            Log.INFO, Log.VERBOSE, Log.DEBUG -> {
                // do nothing
            }
            Log.WARN, Log.ERROR, Log.ASSERT -> {
                crashlyticsManager.log(priority, tag, message, t)
            }
        }
    }
}