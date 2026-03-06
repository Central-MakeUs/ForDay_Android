package com.forday.app.domain.repository

import com.forday.app.domain.model.AppVersionPolicyDomain

interface AppVersionPolicyRepository {
    suspend fun getAppVersionPolicy(platform: String, appVersion: String, build: Int): AppVersionPolicyDomain
}
