package com.forday.app.presentation.onboarding.splash

import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.dayn.forday.BuildConfig
import com.forday.app.domain.model.AppUpdateType
import com.forday.app.domain.usecase.GetAppVersionPolicyUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import com.forday.app.presentation.onboarding.splash.navigation.Splash
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getAppVersionPolicyUseCase: GetAppVersionPolicyUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<Unit>() {

    private val _uiState: MutableStateFlow<SplashUiState> = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.toStateIn()

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: SplashAction) {
        when (action) {
            is SplashAction.SetRealRoute -> setRealRoute(action.realRoute)
            is SplashAction.Dismiss -> dismiss()
        }
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun setRealRoute(realRoute: NavKey) {
        _uiState.update { it.copy(realRoute = realRoute) }
        computeEffectiveRoute()
    }

    private fun dismiss() {
        val realRoute = _uiState.value.realRoute ?: return
        _uiState.update { it.copy(effectiveRoute = realRoute) }
    }

    private fun fetchAppVersionPolicy() = viewModelScope.launch {
        flow {
            emit(
                getAppVersionPolicyUseCase(
                    platform = "ANDROID",
                    appVersion = BuildConfig.VERSION_NAME,
                    build = BuildConfig.VERSION_CODE
                )
            )
        }.httpCatch(tag = "fetchAppVersionPolicy") { errorData ->
            _uiState.update { it.copy(isLoading = false) }
            snackbarManager.show(errorData.message)
        }.collect { result ->
            val data = result.data
            _uiState.update {
                it.copy(
                    isLoading = false,
                    updateType = data?.updateType ?: it.updateType,
                    storeUrl = data?.storeUrl ?: it.storeUrl,
                    message = data?.message ?: it.message,
                )
            }
            computeEffectiveRoute()
        }
    }

    private fun computeEffectiveRoute() {
        val state = _uiState.value
        if (state.isLoading || state.realRoute == null) return

        val effectiveRoute: NavKey = when (state.updateType) {
            AppUpdateType.NONE -> state.realRoute
            else -> Splash
        }
        _uiState.update { it.copy(effectiveRoute = effectiveRoute) }
    }
}
