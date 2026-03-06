package com.forday.app.domain.repository

import com.forday.app.domain.model.CancelAccountDomain
import com.forday.app.domain.model.GuestLoginDataDomain
import com.forday.app.domain.model.KakaoLoginDomain
import com.forday.app.domain.model.LogoutDomain
import com.forday.app.domain.model.SwitchAccountDomain
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun kakaoLogin(kakaoAccessToken: String): Result<KakaoLoginDomain>
    suspend fun kakaoLogout(): Result<Unit>
    suspend fun guestLogin(): Result<GuestLoginDataDomain>
    fun getAccessToken(): Flow<String?>
    fun getSocialType(): Flow<String?>
    fun getGuestUserId(): Flow<String?>
    fun getIsOnboardingCompleted(): Flow<Boolean?>
    fun getIsNicknameSet(): Flow<Boolean?>
    suspend fun saveIsOnboardingCompleted(isOnboardingCompleted: Boolean)
    suspend fun saveIsNicknameSet(isNicknameSet: Boolean)
    fun getHasSeenIntro(): Flow<Boolean>
    suspend fun saveHasSeenIntro(hasSeen: Boolean)
    suspend fun removeAccessToken()  // 지우지 말고 로그아웃 시 사용
    suspend fun switchAccount(socialType: String, kakaoAccessToken: String): Result<SwitchAccountDomain>
    suspend fun logout(): Result<LogoutDomain>
    suspend fun cancelAccount(): Result<CancelAccountDomain>
}