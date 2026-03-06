package com.forday.app.core.logger.analytics

interface AnalyticsManager {
    fun logEvent(name: String, params: Map<String, Any?>? = null)
    fun logEvent(event: AnalyticsEvent)
    fun setUserId(userId: String)
    fun setUserProperty(name: String, value: String)
}