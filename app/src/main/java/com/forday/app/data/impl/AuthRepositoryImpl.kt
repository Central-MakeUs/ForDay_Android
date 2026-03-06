package com.forday.app.data.impl

import android.util.Log
import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.data.model.toDomain
import com.forday.app.data.remote.AuthDataSource
import com.forday.app.domain.model.CancelAccountDomain
import com.forday.app.domain.model.GuestLoginDataDomain
import com.forday.app.domain.model.KakaoLoginDomain
import com.forday.app.domain.model.LogoutDomain
import com.forday.app.domain.model.SwitchAccountDomain
import com.forday.app.domain.repository.AuthRepository
import com.forday.app.remote.model.request.GuestLoginRequest
import com.forday.app.remote.model.request.KakaoLoginRequest
import com.forday.app.remote.model.request.SwitchAccountRequest
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.runCatching

internal class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : AuthRepository {
    override suspend fun kakaoLogin(kakaoAccessToken: String) : Result<KakaoLoginDomain> =
        runCatching {
            try {
                Log.e("AuthRepository", "kakaoLogin: start")
                Timber.d("kakaoLogin: start")
                val responseEntity = authDataSource.kakaoLogin(KakaoLoginRequest(kakaoAccessToken))
                Log.e("AuthRepository", "kakaoLogin: remote success")
                Timber.d("kakaoLogin: remote success")

                val loginData = responseEntity.toDomain()
                Log.e("AuthRepository", "kakaoLogin: mapping success")
                Timber.d("kakaoLogin: mapping success")

                // 로컬에 토큰 저장
                userLocalDataSource.saveKakaoToken(
                    loginData.data.accessToken,
                    loginData.data.refreshToken,
                    loginData.data.socialType
                )
                Log.e("AuthRepository", "kakaoLogin: saveKakaoToken success")
                Timber.d("kakaoLogin: saveKakaoToken success")

                userLocalDataSource.saveNickname(loginData.data.nickname)
                Log.e("AuthRepository", "kakaoLogin: saveNickname success")
                Timber.d("kakaoLogin: saveNickname success")

                userLocalDataSource.saveIsOnboardingCompleted(loginData.data.isOnboardingCompleted)
                Log.e("AuthRepository", "kakaoLogin: saveIsOnboardingCompleted success")
                Timber.d("kakaoLogin: saveIsOnboardingCompleted success")

                userLocalDataSource.saveIsNicknameSet(loginData.data.isNicknameSet)
                Log.e("AuthRepository", "kakaoLogin: saveIsNicknameSet success")
                Timber.d("kakaoLogin: saveIsNicknameSet success")

                userLocalDataSource.saveOnboardingData(
                    loginData.data.onboardingData?.hobbyCardId?.toLong(),
                    loginData.data.onboardingData?.hobbyName,
                    loginData.data.onboardingData?.hobbyTimeMinutes,
                    loginData.data.onboardingData?.hobbyPurpose,
                    loginData.data.onboardingData?.executionCount,
                    loginData.data.onboardingData?.isDurationSet == true
                )
                Log.e("AuthRepository", "kakaoLogin: saveOnboardingData success")
                Timber.d("kakaoLogin: saveOnboardingData success")

                loginData.data.onboardingData?.id?.let { entityId ->
                    userLocalDataSource.saveCreatedHobbyId(entityId.toLong())
                }

                Log.e("AuthRepository", "kakaoLogin: done")
                Timber.d("kakaoLogin: done")
                loginData
            } catch (e: Exception) {
                Log.e("AuthRepository", "kakaoLogin: failed at repository layer", e)
                Timber.e(e, "kakaoLogin: failed at repository layer")
                throw e
            }
        }

    override suspend fun kakaoLogout(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun guestLogin(): Result<GuestLoginDataDomain> =
        runCatching {
            val userId = userLocalDataSource.guestIdFlow.first()
            Timber.d("guestLogin: guestUserId=$userId")
            val loginResponse = authDataSource.guestLogin(GuestLoginRequest(userId))
            Timber.d("guestLogin: accessToken=${loginResponse.data.accessToken}, onboardingCompleted=${loginResponse.data.onboardingCompleted}, nicknameSet=${loginResponse.data.nicknameSet}")

            // 토큰 + guestUserId 저장
            userLocalDataSource.saveGuestTokenAndId(
                loginResponse.data.accessToken,
                loginResponse.data.refreshToken,
                loginResponse.data.userId,
                loginResponse.data.socialType
            )
            // 온보딩/닉네임 상태 저장 (카카오 로그인과 동일)
            userLocalDataSource.saveIsOnboardingCompleted(loginResponse.data.onboardingCompleted)
            userLocalDataSource.saveIsNicknameSet(loginResponse.data.nicknameSet)
            userLocalDataSource.saveNickname(loginResponse.data.nickname)
            userLocalDataSource.saveOnboardingData(
                loginResponse.data.onboardingData?.id,
                loginResponse.data.onboardingData?.hobbyName,
                loginResponse.data.onboardingData?.hobbyTimeMinutes,
                loginResponse.data.onboardingData?.hobbyPurpose,
                loginResponse.data.onboardingData?.executionCount,
                loginResponse.data.onboardingData?.durationSet ?: false
            )
            loginResponse.data.onboardingData?.id?.let { entityId ->
                userLocalDataSource.saveCreatedHobbyId(entityId)
            }
            Timber.d("guestLogin: all data saved")
            loginResponse.data.toData().toDomain()
        }

    override fun getAccessToken(): Flow<String?> =
        userLocalDataSource.getAccessToken()

    override fun getSocialType(): Flow<String?> =
        userLocalDataSource.getSocialType()

    override fun getGuestUserId(): Flow<String?> =
        userLocalDataSource.guestIdFlow

    override fun getIsOnboardingCompleted(): Flow<Boolean> =
        userLocalDataSource.getIsOnboardingCompleted()

    override fun getIsNicknameSet(): Flow<Boolean?> =
        userLocalDataSource.getIsNicknameSet()


    override suspend fun saveIsOnboardingCompleted(isOnboardingCompleted: Boolean) =
        userLocalDataSource.saveIsOnboardingCompleted(isOnboardingCompleted)


    override suspend fun saveIsNicknameSet(isNicknameSet: Boolean) =
        userLocalDataSource.saveIsNicknameSet(isNicknameSet)

    override fun getHasSeenIntro(): Flow<Boolean> =
        userLocalDataSource.hasSeenIntroFlow

    override suspend fun saveHasSeenIntro(hasSeen: Boolean) =
        userLocalDataSource.saveHasSeenIntro(hasSeen)


    override suspend fun removeAccessToken() {
        userLocalDataSource.removeAccessToken()
    }

    override suspend fun switchAccount(
        socialType: String,
        kakaoAccessToken: String
    ): Result<SwitchAccountDomain> = runCatching {
        val loginData = authDataSource.switchAccount(SwitchAccountRequest(socialType, kakaoAccessToken)).toDomain()
        Timber.d("@@@@@@@switchAccount loginResponse: ${loginData.accessToken}")
        userLocalDataSource.saveKakaoToken(
            loginData.accessToken,
            loginData.refreshToken,
            loginData.socialType
        )
        Timber.d("@@@@@@@switchAccount 토큰 저장 완료: ${loginData.accessToken}"+" ${loginData.refreshToken}")
        loginData
    }

    override suspend fun logout() = runCatching {
        val logoutData = authDataSource.logout().toDomain()
        userLocalDataSource.removeTokenAndLoginType()
        userLocalDataSource.removeUserInfo()
        logoutData
    }

    override suspend fun cancelAccount() = runCatching {
        val socialType = userLocalDataSource.getSocialType().first()
        if (socialType == "KAKAO") {
            runCatching {
                suspendCancellableCoroutine<Unit> { continuation ->
                    UserApiClient.instance.unlink { error ->
                        if (error != null) Timber.e("Kakao unlink failed: $error")
                        else Timber.d("Kakao unlink success")
                        continuation.resume(Unit)
                    }
                }
            }.onFailure { Timber.e("Kakao unlink error: $it") }
        }
        val cancelAccountData = authDataSource.cancelAccount().toDomain()
        userLocalDataSource.removeTokenAndLoginType()
        userLocalDataSource.removeUserInfo()
        userLocalDataSource.removeGuestId()
        cancelAccountData
    }

}