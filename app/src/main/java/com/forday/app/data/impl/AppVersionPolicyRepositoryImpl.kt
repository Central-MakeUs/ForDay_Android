package com.forday.app.data.impl

import com.forday.app.data.remote.AppVersionPolicyDataSource
import com.forday.app.domain.model.AppVersionPolicyDomain
import com.forday.app.domain.repository.AppVersionPolicyRepository
import javax.inject.Inject

class AppVersionPolicyRepositoryImpl @Inject constructor(
    private val appVersionPolicyDataSource: AppVersionPolicyDataSource
) : AppVersionPolicyRepository {

    override suspend fun getAppVersionPolicy(platform: String, appVersion: String, build: Int): AppVersionPolicyDomain =
        appVersionPolicyDataSource.getAppVersionPolicy(platform, appVersion, build).toDomain()
}
