package com.forday.app.core.logger.crashlytics

interface CrashlyticsManager {
    fun setUp()
    fun setUserId(userId: String)
    fun log(priority: Int, tag: String?, message: String, t: Throwable?)
}