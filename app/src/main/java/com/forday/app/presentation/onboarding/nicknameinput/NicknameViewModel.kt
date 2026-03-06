package com.forday.app.presentation.onboarding.nicknameinput

import androidx.lifecycle.viewModelScope
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.core.session.AuthManager
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.GetIsNicknameDuplicateUseCase
import com.forday.app.domain.usecase.RegisterNicknameUseCase
import com.forday.app.domain.usecase.SaveNicknameUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NicknameUiState(
    val isLoading: Boolean = false,
    val nicknameCheckMessage: String = "",
    val isNicknameChecked: Boolean = false,
)

sealed interface NicknameSideEffect {
    data object RegisterSuccess : NicknameSideEffect
}

sealed interface NicknameAction {
    data class CheckNicknameDuplicate(val nickname: String) : NicknameAction
    data class RegisterNickname(val nickname: String?) : NicknameAction
    data object ResetNicknameCheck : NicknameAction
    data class SaveIsNicknameSet(val value: Boolean) : NicknameAction
}

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val getIsNicknameDuplicateUseCase: GetIsNicknameDuplicateUseCase,
    private val registerNicknameUseCase: RegisterNicknameUseCase,
    private val saveNicknameUseCase: SaveNicknameUseCase,
    private val authManager: AuthManager,
    private val analyticsManager: AnalyticsManager,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<NicknameSideEffect>() {

    private val _uiState = MutableStateFlow(NicknameUiState())
    val uiState: StateFlow<NicknameUiState> = _uiState.toStateIn()

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: NicknameAction) {
        when (action) {
            is NicknameAction.CheckNicknameDuplicate -> getIsNicknameDuplicate(action.nickname)
            is NicknameAction.RegisterNickname -> registerNickname(action.nickname)
            is NicknameAction.ResetNicknameCheck -> resetNicknameCheck()
            is NicknameAction.SaveIsNicknameSet -> saveIsNicknameSet(action.value)
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

    private fun getIsNicknameDuplicate(nickname: String) = viewModelScope.launch {
        flow {
            emit(getIsNicknameDuplicateUseCase(nickname))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { result ->
            _uiState.update {
                it.copy(
                    nicknameCheckMessage = result.data.message,
                    isNicknameChecked = result.data.available,
                )
            }
        }
    }

    private fun registerNickname(nickname: String?) = viewModelScope.launch {
        flow {
            emit(registerNicknameUseCase(nickname))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { data ->
            if (data.isSuccess) {
                nickname?.let { saveNicknameUseCase(it) }
                _sideEffectChannel.send(NicknameSideEffect.RegisterSuccess)
            }
        }
    }

    private fun resetNicknameCheck() {
        _uiState.update {
            it.copy(nicknameCheckMessage = "", isNicknameChecked = false)
        }
    }

    private fun saveIsNicknameSet(value: Boolean) = viewModelScope.launch {
        authManager.saveIsNicknameSet(value)
    }
}
