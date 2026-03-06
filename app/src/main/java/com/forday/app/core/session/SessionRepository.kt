package com.forday.app.core.session

import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.domain.model.GuestLoginDataDomain
import com.forday.app.domain.model.KakaoLoginDomain
import com.forday.app.domain.repository.AuthRepository
import com.forday.app.presentation.common.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource,
    private val authRepository: AuthRepository,
    private val snackbarManager: SnackbarManager,
    @ApplicationScope private val scope: CoroutineScope,
) {
    private val _sessionState = MutableStateFlow(SessionState())
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    suspend fun loadInitialState() {
        try {
            val accessToken = userLocalDataSource.accessTokenFlow.first()
            val isOnboardingCompleted = userLocalDataSource.isOnboardingCompletedFlow.first()
            val isNicknameSet = userLocalDataSource.isNicknameSetFlow.first()
            val hasSeenIntro = userLocalDataSource.hasSeenIntroFlow.first()
            val socialType = userLocalDataSource.socialTypeFlow.first()
            val guestUserId = userLocalDataSource.guestIdFlow.first()

            _sessionState.update {
                it.copy(
                    accessToken = accessToken,
                    isOnboardingCompleted = isOnboardingCompleted,
                    isNicknameSet = isNicknameSet,
                    hasSeenIntro = hasSeenIntro,
                    socialType = socialType,
                    guestUserId = guestUserId,
                    isInitialized = true,
                )
            }

            startObservingDataStore()
        } catch (e: Exception) {
            Timber.e(e, "loadInitialState failed")
            _sessionState.update { it.copy(isInitialized = true) }
        }
    }

    private fun startObservingDataStore() {
        scope.launch {
            combine(
                userLocalDataSource.accessTokenFlow,
                userLocalDataSource.isOnboardingCompletedFlow,
                userLocalDataSource.isNicknameSetFlow,
                userLocalDataSource.hasSeenIntroFlow,
                userLocalDataSource.socialTypeFlow,
            ) { accessToken, onboarding, nickname, intro, social ->
                SessionUpdate(accessToken, onboarding, nickname, intro, social)
            }.catch { Timber.e(it, "DataStore observation error") }
                .collect { update ->
                    _sessionState.update {
                        if (it.isTransitioning) return@collect
                        it.copy(
                            accessToken = update.accessToken,
                            isOnboardingCompleted = update.isOnboardingCompleted,
                            isNicknameSet = update.isNicknameSet,
                            hasSeenIntro = update.hasSeenIntro,
                            socialType = update.socialType,
                        )
                    }
                }
        }
    }

    suspend fun loginWithKakaoToken(kakaoAccessToken: String): Result<KakaoLoginDomain> {
        _sessionState.update { it.copy(isTransitioning = true) }
        return try {
            val result = authRepository.kakaoLogin(kakaoAccessToken)
            result.onSuccess { loginData ->
                _sessionState.update {
                    it.copy(
                        accessToken = loginData.data.accessToken,
                        isOnboardingCompleted = loginData.data.isOnboardingCompleted,
                        isNicknameSet = loginData.data.isNicknameSet,
                        socialType = loginData.data.socialType,
                        isLoginSuccess = true,
                        isNewUser = loginData.data.isNewUser,
                        isTransitioning = false,
                    )
                }
            }.onFailure {
                _sessionState.update { it.copy(isTransitioning = false) }
            }
            result
        } catch (e: Exception) {
            _sessionState.update { it.copy(isTransitioning = false) }
            Result.failure(e)
        }
    }

    suspend fun loginAsGuest(): Result<GuestLoginDataDomain> {
        _sessionState.update { it.copy(isTransitioning = true) }
        return try {
            val result = authRepository.guestLogin()
            result.onSuccess { data ->
                _sessionState.update {
                    it.copy(
                        accessToken = data.accessToken,
                        isOnboardingCompleted = data.onboardingCompleted,
                        isNicknameSet = data.nicknameSet,
                        socialType = data.socialType,
                        guestUserId = data.userId,
                        isLoginSuccess = true,
                        isNewUser = data.isNewUser,
                        isTransitioning = false,
                    )
                }
            }.onFailure {
                _sessionState.update { it.copy(isTransitioning = false) }
            }
            result
        } catch (e: Exception) {
            _sessionState.update { it.copy(isTransitioning = false) }
            Result.failure(e)
        }
    }

    suspend fun autoGuestReLoginIfNeeded(): Boolean {
        val state = _sessionState.value
        if (state.accessToken == null && state.socialType == "GUEST" && state.guestUserId != null) {
            Timber.d("autoGuestReLoginIfNeeded: attempting re-login")
            val result = loginAsGuest()
            return result.isSuccess
        }
        return true
    }

    suspend fun saveIsOnboardingCompleted(value: Boolean) {
        userLocalDataSource.saveIsOnboardingCompleted(value)
        _sessionState.update { it.copy(isOnboardingCompleted = value) }
    }

    suspend fun saveIsNicknameSet(value: Boolean) {
        userLocalDataSource.saveIsNicknameSet(value)
        _sessionState.update { it.copy(isNicknameSet = value) }
    }

    suspend fun saveHasSeenIntro(value: Boolean) {
        userLocalDataSource.saveHasSeenIntro(value)
        _sessionState.update { it.copy(hasSeenIntro = value) }
    }

    suspend fun resetForNewSession() {
        _sessionState.update { it.copy(isTransitioning = true) }
        try {
            userLocalDataSource.removeTokenAndLoginType()
            userLocalDataSource.removeUserInfo()
            userLocalDataSource.removeOnboardingData()
        } catch (e: Exception) {
            Timber.e(e, "resetForNewSession error")
        }
        _sessionState.update {
            SessionState(
                isInitialized = true,
                hasSeenIntro = it.hasSeenIntro,
            )
        }
    }

    fun resetLoginFlags() {
        _sessionState.update {
            it.copy(isLoginSuccess = false, isNewUser = null)
        }
    }

    private data class SessionUpdate(
        val accessToken: String?,
        val isOnboardingCompleted: Boolean,
        val isNicknameSet: Boolean,
        val hasSeenIntro: Boolean,
        val socialType: String?,
    )
}
