package com.forday.app.remote.impl

import com.forday.app.data.model.AppVersionPolicyEntity
import com.forday.app.data.remote.AppVersionPolicyDataSource
import com.forday.app.remote.api.service.AppVersionPolicyApi
import javax.inject.Inject

class AppVersionPolicyDataSourceImpl @Inject constructor(
    private val appVersionPolicyApi: AppVersionPolicyApi
) : AppVersionPolicyDataSource {

    override suspend fun getAppVersionPolicy(platform: String, appVersion: String, build: Int): AppVersionPolicyEntity =
        appVersionPolicyApi.getAppVersionPolicy(platform, appVersion, build).toData()
}
