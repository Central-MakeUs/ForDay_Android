package com.forday.app.presentation.onboarding.login

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.core.session.AuthManager
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val analyticsManager: AnalyticsManager,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<LoginSideEffect>() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.toStateIn()

    val sessionState get() = authManager.session.sessionState

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.LoginWithKakao -> loginWithKakao(action.context)
            is LoginAction.LoginWithGuest -> loginWithGuest()
        }
    }

    // ── Analytics ─────────────────────────────────────────────────────
    fun logEvent(event: String) {
        analyticsManager.logEvent(event)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun loginWithKakao(context: Context) {
        val kakao = UserApiClient.instance

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null || token == null) {
                val errorMessage = error?.toUserMessage(UserMessageCategory.AUTH)
                    ?: "로그인에 실패했어요. 잠시 후 다시 시도해주세요."
                snackbarManager.show(errorMessage)
            } else {
                loginIntoApp(token.accessToken)
            }
        }

        if (kakao.isKakaoTalkLoginAvailable(context)) {
            kakao.loginWithKakaoTalk(context) { token, error ->
                if (error != null || token == null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        snackbarManager.show(error.toUserMessage(UserMessageCategory.AUTH))
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else {
                    loginIntoApp(token.accessToken)
                }
            }
        } else {
            kakao.loginWithKakaoAccount(context, callback = callback)
        }
    }

    private fun loginWithGuest() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            authManager.loginAsGuest()
                .onSuccess { data ->
                    _uiState.update { it.copy(isLoading = false) }
                    resolveLoginNavigation(
                        isNewUser = data.isNewUser,
                        isOnboardingCompleted = data.onboardingCompleted,
                        isNicknameSet = data.nicknameSet,
                    )
                }
                .onFailure { error ->
                    val errorMessage = error.toUserMessage(UserMessageCategory.AUTH)
                    _uiState.update { it.copy(isLoading = false) }
                    snackbarManager.show(errorMessage)
                }
        } catch (e: Exception) {
            val errorMessage = e.toUserMessage(UserMessageCategory.AUTH)
            _uiState.update { it.copy(isLoading = false) }
            snackbarManager.show(errorMessage)
        }
    }

    private fun loginIntoApp(kakaoAccessToken: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            authManager.loginWithKakaoToken(kakaoAccessToken)
                .onSuccess { loginData ->
                    _uiState.update { it.copy(isLoading = false) }
                    resolveLoginNavigation(
                        isNewUser = loginData.data.isNewUser,
                        isOnboardingCompleted = loginData.data.isOnboardingCompleted,
                        isNicknameSet = loginData.data.isNicknameSet,
                    )
                }
                .onFailure { error ->
                    val errorMessage = error.toUserMessage(UserMessageCategory.AUTH)
                    _uiState.update { it.copy(isLoading = false) }
                    snackbarManager.show(errorMessage)
                }
        } catch (e: Exception) {
            val errorMessage = e.toUserMessage(UserMessageCategory.AUTH)
            _uiState.update { it.copy(isLoading = false) }
            snackbarManager.show(errorMessage)
        }
    }

    private suspend fun resolveLoginNavigation(
        isNewUser: Boolean?,
        isOnboardingCompleted: Boolean?,
        isNicknameSet: Boolean?,
    ) {
        val destination = when {
            isNewUser == true -> LoginDestination.Onboarding
            isOnboardingCompleted == false -> LoginDestination.Onboarding
            isOnboardingCompleted == true && isNicknameSet == false -> LoginDestination.Nickname
            isOnboardingCompleted == true && isNicknameSet == true -> LoginDestination.Home
            else -> return
        }
        _sideEffectChannel.send(LoginSideEffect.NavigateTo(destination))
    }
}
