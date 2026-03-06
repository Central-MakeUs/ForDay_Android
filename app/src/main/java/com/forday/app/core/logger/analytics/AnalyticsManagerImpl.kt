package com.forday.app.core.logger.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AnalyticsManagerImpl @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsManager {

    override fun logEvent(name: String, params: Map<String, Any?>?) {
        val bundle = params?.let { mapToBundle(it) }
        firebaseAnalytics.logEvent(name, bundle)
    }

    override fun logEvent(event: AnalyticsEvent) {
        val bundle = Bundle().apply {
            putString("description", event.description)
            if (event.params.isNotEmpty()) {
                putAll(mapToBundle(event.params))
            }
        }
        firebaseAnalytics.logEvent(event.id, bundle)
    }

    override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    private fun mapToBundle(map: Map<String, Any?>): Bundle {
        return Bundle().apply {
            map.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
    }
}