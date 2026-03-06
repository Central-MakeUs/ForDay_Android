package com.forday.app.data.remote

import com.forday.app.data.model.CancelAccountEntity
import com.forday.app.data.model.KakaoLoginEntity
import com.forday.app.data.model.LogoutEntity
import com.forday.app.data.model.SwitchAccountEntity
import com.forday.app.remote.model.request.GuestLoginRequest
import com.forday.app.remote.model.request.KakaoLoginRequest
import com.forday.app.remote.model.request.SwitchAccountRequest
import com.forday.app.remote.model.response.GuestLoginResponse
import com.forday.app.remote.model.response.LogoutResponse

interface AuthDataSource {
    suspend fun kakaoLogin(kakaoAccessToken: KakaoLoginRequest): KakaoLoginEntity
    suspend fun guestLogin(guestUserId: GuestLoginRequest): GuestLoginResponse
    suspend fun switchAccount(switchAccountRequest: SwitchAccountRequest): SwitchAccountEntity
    suspend fun logout(): LogoutEntity
    suspend fun cancelAccount(): CancelAccountEntity
}