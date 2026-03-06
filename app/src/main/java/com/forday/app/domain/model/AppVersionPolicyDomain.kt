package com.forday.app.domain.model

data class AppVersionPolicyDomain(
    val status: Int,
    val success: Boolean,
    val data: AppVersionPolicyDataDomain?
)

data class AppVersionPolicyDataDomain(
    val policyVersion: Int,
    val platform: String,
    val current: AppVersionInfoDomain,
    val minSupported: AppVersionInfoDomain,
    val latest: AppVersionInfoDomain,
    val updateType: AppUpdateType,
    val storeUrl: String,
    val message: String
)

data class AppVersionInfoDomain(
    val version: String,
    val build: Int
)

enum class AppUpdateType {
    BLOCK,
    FORCE,
    RECOMMEND,
    NONE
}
