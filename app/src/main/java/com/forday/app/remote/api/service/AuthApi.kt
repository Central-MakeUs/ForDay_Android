package com.forday.app.remote.api.service

import com.forday.app.remote.model.request.GuestLoginRequest
import com.forday.app.remote.model.request.KakaoLoginRequest
import com.forday.app.remote.model.request.RefreshRequest
import com.forday.app.remote.model.request.SwitchAccountRequest
import com.forday.app.remote.model.response.AccessTokenResponse
import com.forday.app.remote.model.response.GuestLoginResponse
import com.forday.app.remote.model.response.LogoutResponse
import com.forday.app.remote.model.response.SwitchAccountResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/kakao")
    suspend fun kakaoLogin(
        @Body params: KakaoLoginRequest
    ): Response<ResponseBody>

    @POST("/auth/guest")
    suspend fun guestLogin(
        @Body request: GuestLoginRequest
    ): GuestLoginResponse
}