package com.forday.app.remote.api.service

import com.forday.app.remote.model.response.AppVersionPolicyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AppVersionPolicyApi {

    @GET("/app/version-policy")
    suspend fun getAppVersionPolicy(
        @Query("platform") platform: String,
        @Query("appVersion") appVersion: String,
        @Query("build") build: Int
    ): AppVersionPolicyResponse
}
