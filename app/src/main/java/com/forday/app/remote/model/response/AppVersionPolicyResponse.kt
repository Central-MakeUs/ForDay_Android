package com.forday.app.remote.model.response

import com.forday.app.data.model.AppVersionInfoEntity
import com.forday.app.data.model.AppVersionPolicyDataEntity
import com.forday.app.data.model.AppVersionPolicyEntity
import com.forday.app.remote.RemoteMapper
import com.google.gson.annotations.SerializedName

data class AppVersionPolicyResponse(
    @SerializedName("status") val status: Int?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("data") val data: AppVersionPolicyDataResponse? = null
) : RemoteMapper<AppVersionPolicyEntity> {
    override fun toData(): AppVersionPolicyEntity = AppVersionPolicyEntity(
        status = status ?: 0,
        success = success ?: false,
        data = data?.toData()
    )
}

data class AppVersionPolicyDataResponse(
    @SerializedName("policyVersion") val policyVersion: Int?,
    @SerializedName("platform") val platform: String?,
    @SerializedName("current") val current: AppVersionInfoResponse?,
    @SerializedName("minSupported") val minSupported: AppVersionInfoResponse?,
    @SerializedName("latest") val latest: AppVersionInfoResponse?,
    @SerializedName("update") val update: String?,
    @SerializedName("storeUrl") val storeUrl: String?,
    @SerializedName("message") val message: String?
) : RemoteMapper<AppVersionPolicyDataEntity> {
    override fun toData(): AppVersionPolicyDataEntity = AppVersionPolicyDataEntity(
        policyVersion = policyVersion ?: 0,
        platform = platform ?: "",
        current = current?.toData() ?: AppVersionInfoEntity(version = "", build = 0),
        minSupported = minSupported?.toData() ?: AppVersionInfoEntity(version = "", build = 0),
        latest = latest?.toData() ?: AppVersionInfoEntity(version = "", build = 0),
        update = update ?: "NONE",
        storeUrl = storeUrl ?: "",
        message = message ?: ""
    )
}

data class AppVersionInfoResponse(
    @SerializedName("version") val version: String?,
    @SerializedName("build") val build: Int?
) : RemoteMapper<AppVersionInfoEntity> {
    override fun toData(): AppVersionInfoEntity = AppVersionInfoEntity(
        version = version ?: "",
        build = build ?: 0
    )
}
