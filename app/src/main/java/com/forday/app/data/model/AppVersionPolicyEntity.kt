package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.AppUpdateType
import com.forday.app.domain.model.AppVersionInfoDomain
import com.forday.app.domain.model.AppVersionPolicyDataDomain
import com.forday.app.domain.model.AppVersionPolicyDomain

data class AppVersionPolicyEntity(
    val status: Int,
    val success: Boolean,
    val data: AppVersionPolicyDataEntity?
) : DataMapper<AppVersionPolicyDomain> {
    override fun toDomain(): AppVersionPolicyDomain = AppVersionPolicyDomain(
        status = status,
        success = success,
        data = data?.toDomain()
    )
}

data class AppVersionPolicyDataEntity(
    val policyVersion: Int,
    val platform: String,
    val current: AppVersionInfoEntity,
    val minSupported: AppVersionInfoEntity,
    val latest: AppVersionInfoEntity,
    val update: String,
    val storeUrl: String,
    val message: String
) : DataMapper<AppVersionPolicyDataDomain> {
    override fun toDomain(): AppVersionPolicyDataDomain = AppVersionPolicyDataDomain(
        policyVersion = policyVersion,
        platform = platform,
        current = current.toDomain(),
        minSupported = minSupported.toDomain(),
        latest = latest.toDomain(),
        updateType = AppUpdateType.entries.find { it.name == update } ?: AppUpdateType.NONE,
        storeUrl = storeUrl,
        message = message
    )
}

data class AppVersionInfoEntity(
    val version: String,
    val build: Int
) : DataMapper<AppVersionInfoDomain> {
    override fun toDomain(): AppVersionInfoDomain = AppVersionInfoDomain(
        version = version,
        build = build
    )
}
