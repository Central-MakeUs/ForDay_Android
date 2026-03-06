package com.forday.app.remote.api.service

import com.forday.app.remote.model.request.RefreshRequest
import com.forday.app.remote.model.response.AccessTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {
    @POST("/auth/refresh")
    suspend fun refreshToken(
        @Body params: RefreshRequest
    ): AccessTokenResponse
}