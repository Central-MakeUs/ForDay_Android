package com.forday.app.core.logger.crashlytics

import android.util.Log
import androidx.annotation.StringDef
import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsManagerImpl(
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : CrashlyticsManager {

    override fun setUp() {
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true)
    }

    override fun setUserId(userId: String) {
        firebaseCrashlytics.setUserId(userId)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        tag?.let { firebaseCrashlytics.setCustomKey(KEY_TAG, tag) }
        firebaseCrashlytics.setCustomKey(KEY_PRIORITY, priorityToString(priority))

        t?.let {
            firebaseCrashlytics.log(message)
            firebaseCrashlytics.recordException(it)
        }
    }

    @LogText
    private fun priorityToString(priority: Int): String {
        return when (priority) {
            Log.INFO -> LogText.INFO
            Log.VERBOSE -> LogText.VERBOSE
            Log.DEBUG -> LogText.DEBUG
            Log.WARN -> LogText.WARN
            Log.ERROR -> LogText.ERROR
            Log.ASSERT -> LogText.ASSERT
            else -> LogText.UNKNOWN
        }
    }

    @StringDef(
        value = [
            LogText.INFO,
            LogText.VERBOSE,
            LogText.DEBUG,
            LogText.WARN,
            LogText.ERROR,
            LogText.ASSERT,
            LogText.UNKNOWN
        ]
    )
    annotation class LogText {
        companion object {
            internal const val INFO = "INFO"
            internal const val VERBOSE = "VERBOSE"
            internal const val DEBUG = "DEBUG"
            internal const val WARN = "WARN"
            internal const val ERROR = "ERROR"
            internal const val ASSERT = "ASSERT"
            internal const val UNKNOWN = "Unknown"
        }
    }

    companion object {
        internal const val KEY_TAG = "TAG"
        internal const val KEY_PRIORITY = "PRIORITY"
    }
}