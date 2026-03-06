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

    // cold start 시 1회만 호출됨 (ViewModel init)
    init {
//        Timber.e("@@@@@@@@@fetchAppVersionPolicy 호출")
//        fetchAppVersionPolicy()
    }

    /**
     * AppEntryPoint에서 OnboardingViewModel의 initialRoute가 결정되면 호출.
     * API 응답과 realRoute 모두 준비된 시점에 effectiveRoute를 계산.
     */
    fun setRealRoute(realRoute: NavKey) {
        _uiState.update { it.copy(realRoute = realRoute) }
        computeEffectiveRoute()
    }

    /**
     * RECOMMEND 팝업에서 "나중에" 선택 시 호출.
     * effectiveRoute를 realRoute로 변경 → AppEntryPoint에서 MainFlow 재시작 → Splash 스택 자동 제거.
     */
    fun dismiss() {
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
            Timber.e("@@@@@@@@@fetchAppVersionPolicy 호출 errorData : "+errorData)
            snackbarManager.show(errorData.message)
        }.collect { result ->
            val data = result.data
            Timber.e("@@@@@@@@@fetchAppVersionPolicy 호출 data : "+data)
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

    /**
     * isLoading 완료 + realRoute 설정이 모두 된 시점에 effectiveRoute를 결정.
     * - updateType == NONE → 바로 realRoute로 이동
     * - updateType != NONE → Splash(커스텀 스플래시 + 팝업) 경유
     */
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
