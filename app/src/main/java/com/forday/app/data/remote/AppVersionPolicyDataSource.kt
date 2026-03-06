package com.forday.app.data.remote

import com.forday.app.data.model.AppVersionPolicyEntity

interface AppVersionPolicyDataSource {
    suspend fun getAppVersionPolicy(platform: String, appVersion: String, build: Int): AppVersionPolicyEntity
}
