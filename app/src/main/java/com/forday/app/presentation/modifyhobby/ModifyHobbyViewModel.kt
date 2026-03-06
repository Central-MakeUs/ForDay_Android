package com.forday.app.presentation.modifyhobby

import androidx.lifecycle.viewModelScope
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.ChangeHobbyStatusUseCase
import com.forday.app.domain.usecase.GetMyHobbyListUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModifyHobbyViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getMyHobbyListUseCase: GetMyHobbyListUseCase,   // 내 취미 설정 페이지 조회
    private val changeHobbyStatusUseCase: ChangeHobbyStatusUseCase,  // 취미 보관 또는 꺼내기
    private val snackbarManager: SnackbarManager
) : BaseViewModel<ModifyHobbySideEffect>() {

    private val _uiState: MutableStateFlow<ModifyHobbyUiState> = MutableStateFlow(ModifyHobbyUiState())
    val uiState: StateFlow<ModifyHobbyUiState> = _uiState.toStateIn()

    fun fetchMyHobbyList(inProgress: String? = null) = viewModelScope.launch {  // 내 취미 설정 페이지 조회
        _uiState.update { it.copy(isLoading = true) }

        flow {
            emit(getMyHobbyListUseCase(inProgress))
        }.httpCatch("fetchMyHobbyList") { errorData ->
            _uiState.update {
                it.copy(
                    errorData = errorData
                )
            }
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

    fun modifyHobbyStatus(hobbyId: Long, hobbyStatus: String) = viewModelScope.launch {
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

    fun dismissHobbyLimitDialog() {
        _uiState.update { it.copy(showHobbyLimitDialog = false, toastMessage = null) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null, toastTargetTab = null) }
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }
}