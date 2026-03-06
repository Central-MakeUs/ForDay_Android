package com.forday.app.presentation.allsettings

import androidx.lifecycle.viewModelScope
import com.forday.app.domain.repository.AuthRepository
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<SettingsSideEffect>() {

    private val _uiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.toStateIn()

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.Logout -> logout()
            is SettingsAction.CancelAccount -> cancelAccount()
        }
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun logout() = viewModelScope.launch {
        repository.logout()
            .onSuccess { data ->
                if (data.isSuccess) {
                    _sideEffectChannel.send(SettingsSideEffect.LoggedOut)
                } else {
                    snackbarManager.show(data.message)
                }
            }
            .onFailure { throwable ->
                throwable.printStackTrace()
                snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
            }
    }

    private fun cancelAccount() = viewModelScope.launch {
        repository.cancelAccount()
            .onSuccess {
                _sideEffectChannel.send(SettingsSideEffect.AccountCancelled)
            }
            .onFailure { throwable ->
                throwable.printStackTrace()
                snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
            }
    }
}
