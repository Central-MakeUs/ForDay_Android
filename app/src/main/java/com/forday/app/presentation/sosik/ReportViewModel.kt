package com.forday.app.presentation.sosik

import androidx.lifecycle.viewModelScope
import com.forday.app.domain.usecase.BlockUserUseCase
import com.forday.app.domain.usecase.ReportPostingUseCase
import com.forday.app.domain.usecase.ReportUserUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportPostingUseCase: ReportPostingUseCase,
    private val reportUserUseCase: ReportUserUseCase,
    private val blockUserUseCase: BlockUserUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<ReportSideEffect>() {

    private val _uiState: MutableStateFlow<ReportUiState> = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.toStateIn()

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: ReportAction) {
        when (action) {
            is ReportAction.ReportPosting -> reportPosting(action.recordId, action.reason)
            is ReportAction.ReportUser -> reportUser(action.userId, action.reason)
            is ReportAction.BlockUser -> blockUser(action.userId)
        }
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun reportPosting(recordId: Int, reason: String) = viewModelScope.launch {
        flow {
            emit(reportPostingUseCase(recordId, reason))
        }.httpCatch(tag = "reportPosting") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            if (data.success) {
                _uiState.update { it.copy(reportedWriterId = data.data.recordWriterId) }
                _sideEffectChannel.send(ReportSideEffect.ReportPostingSuccess(data.data.recordWriterId))
            } else {
                snackbarManager.show(data.data.message)
            }
        }
    }

    private fun reportUser(userId: String, reason: String) = viewModelScope.launch {
        flow {
            emit(reportUserUseCase(userId, reason))
        }.httpCatch(tag = "reportUser") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            snackbarManager.show(data.data.message)
            _sideEffectChannel.send(ReportSideEffect.ReportUserSuccess)
        }
    }

    private fun blockUser(userId: String) = viewModelScope.launch {
        flow {
            emit(blockUserUseCase(userId))
        }.httpCatch(tag = "blockUser") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            if (data.success) {
                _uiState.update { it.copy(reportedWriterId = "") }
                _sideEffectChannel.send(ReportSideEffect.BlockUserSuccess)
            } else {
                snackbarManager.show(data.data.message)
            }
        }
    }
}
