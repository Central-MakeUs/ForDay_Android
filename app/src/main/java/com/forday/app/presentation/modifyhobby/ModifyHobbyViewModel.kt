package com.forday.app.presentation.modifyhobby

import androidx.lifecycle.viewModelScope
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import com.forday.app.domain.usecase.ChangeHobbyStatusUseCase
import com.forday.app.domain.usecase.GetMyHobbyListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyHobbyViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getMyHobbyListUseCase: GetMyHobbyListUseCase,
    private val changeHobbyStatusUseCase: ChangeHobbyStatusUseCase,
    private val snackbarManager: SnackbarManager
) : BaseViewModel<ModifyHobbySideEffect>() {

    private val _uiState: MutableStateFlow<ModifyHobbyUiState> = MutableStateFlow(ModifyHobbyUiState())
    val uiState: StateFlow<ModifyHobbyUiState> = _uiState.toStateIn()

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: ModifyHobbyAction) {
        when (action) {
            is ModifyHobbyAction.FetchMyHobbyList -> fetchMyHobbyList(action.inProgress)
            is ModifyHobbyAction.ModifyHobbyStatus -> modifyHobbyStatus(action.hobbyId, action.hobbyStatus)
            is ModifyHobbyAction.DismissHobbyLimitDialog -> dismissHobbyLimitDialog()
            is ModifyHobbyAction.ClearToast -> clearToast()
        }
    }

    // ── Analytics ─────────────────────────────────────────────────────
    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun fetchMyHobbyList(inProgress: String? = null) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        flow {
            emit(getMyHobbyListUseCase(inProgress))
        }.httpCatch("fetchMyHobbyList") { errorData ->
            _uiState.update { it.copy(errorData = errorData) }
        }.collect { data ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentHobbyStatus = data.data.currentHobbyStatus,
                    inProgressHobbyCount = data.data.inProgressHobbyCount,
                    archivedHobbyCount = data.data.archivedHobbyCount,
                    hobbies = data.data.hobbies.map { it.toPresentation() },
                    errorData = null
                )
            }
        }
    }

    private fun modifyHobbyStatus(hobbyId: Long, hobbyStatus: String) = viewModelScope.launch {
        flow {
            emit(changeHobbyStatusUseCase(hobbyId, hobbyStatus))
        }.httpCatch(tag = "modifyHobbyStatus") { errorData ->
            when (errorData.errorClassName) {
                "MAX_IN_PROGRESS_HOBBY_EXCEEDED" -> _uiState.update { it.copy(showHobbyLimitDialog = true, toastMessage = errorData.message) }
                else -> snackbarManager.show(errorData.message)
            }
        }.collect { data ->
            fetchMyHobbyList(_uiState.value.currentHobbyStatus)
            _uiState.update {
                it.copy(
                    toastMessage = data.data.message,
                    toastTargetTab = if (hobbyStatus == "ARCHIVED") "ARCHIVED" else "IN_PROGRESS"
                )
            }
        }
    }

    private fun dismissHobbyLimitDialog() {
        _uiState.update { it.copy(showHobbyLimitDialog = false, toastMessage = null) }
    }

    private fun clearToast() {
        _uiState.update { it.copy(toastMessage = null, toastTargetTab = null) }
    }
}
