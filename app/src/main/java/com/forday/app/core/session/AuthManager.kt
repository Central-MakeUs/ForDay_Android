package com.forday.app.core.session

import androidx.navigation3.runtime.NavKey
import com.forday.app.domain.model.AppUpdateType
import com.forday.app.domain.model.GuestLoginDataDomain
import com.forday.app.domain.model.KakaoLoginDomain
import com.forday.app.presentation.common.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    val session: SessionRepository,
    val versionPolicy: VersionPolicyProvider,
    private val routeResolver: RouteResolver,
    private val authEventBus: AuthEventBus,
    private val snackbarManager: SnackbarManager,
    @ApplicationScope private val scope: CoroutineScope,
) {
    private val _isAppReady = MutableStateFlow(false)
    val isAppReady: StateFlow<Boolean> = _isAppReady.asStateFlow()

    private val _effectiveRoute = MutableStateFlow<NavKey?>(null)
    val effectiveRoute: StateFlow<NavKey?> = _effectiveRoute.asStateFlow()

    private val _events = MutableSharedFlow<AuthManagerEvent>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<AuthManagerEvent> = _events.asSharedFlow()

    private val initialized = AtomicBoolean(false)

    fun initialize() {
        if (!initialized.compareAndSet(false, true)) return

        scope.launch {
            session.loadInitialState()

            coroutineScope {
                launch { session.autoGuestReLoginIfNeeded() }
                launch { versionPolicy.checkVersionPolicy() }
            }

            computeEffectiveRoute()
            _isAppReady.value = true

            observeAuthEvents()
        }
    }

    private fun computeEffectiveRoute() {
        val sessionState = session.sessionState.value
        val versionState = versionPolicy.state.value

        val realRoute = routeResolver.resolve(sessionState)

        _effectiveRoute.value = when (versionState.updateType) {
            AppUpdateType.FORCE, AppUpdateType.BLOCK -> null
            else -> realRoute
        }
    }

    fun recomputeRoute() {
        computeEffectiveRoute()
    }

    suspend fun loginWithKakaoToken(kakaoAccessToken: String): Result<KakaoLoginDomain> {
        return session.loginWithKakaoToken(kakaoAccessToken)
    }

    suspend fun loginAsGuest(): Result<GuestLoginDataDomain> {
        return session.loginAsGuest()
    }

    suspend fun guestAutoReLogin() {
        val success = session.autoGuestReLoginIfNeeded()
        if (success) {
            _events.emit(AuthManagerEvent.GuestReLoginSuccess)
        } else {
            _events.emit(AuthManagerEvent.GuestReLoginFailed)
        }
    }

    suspend fun resetForNewSession() {
        session.resetForNewSession()
    }

    fun dismissUpdatePrompt() {
        versionPolicy.dismissRecommendUpdate()
        computeEffectiveRoute()
    }

    suspend fun saveIsOnboardingCompleted(value: Boolean) {
        session.saveIsOnboardingCompleted(value)
    }

    suspend fun saveIsNicknameSet(value: Boolean) {
        session.saveIsNicknameSet(value)
    }

    suspend fun saveHasSeenIntro(value: Boolean) {
        session.saveHasSeenIntro(value)
    }

    fun resetLoginFlags() {
        session.resetLoginFlags()
    }

    private fun observeAuthEvents() {
        scope.launch {
            authEventBus.events.collect { event ->
                when (event) {
                    AuthEvent.Expired -> {
                        val state = session.sessionState.value
                        if (state.socialType == "GUEST") {
                            guestAutoReLogin()
                        } else {
                            _events.emit(AuthManagerEvent.SessionExpired)
                        }
                    }
                }
            }
        }
    }
}
