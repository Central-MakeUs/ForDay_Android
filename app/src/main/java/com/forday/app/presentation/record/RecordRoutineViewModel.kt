package com.forday.app.presentation.record

import androidx.lifecycle.viewModelScope
import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.DeleteS3ImageUseCase
import com.forday.app.domain.usecase.GetMyRoutineRecordDetailUseCase
import com.forday.app.domain.usecase.GetPresignedUrlUseCase
import com.forday.app.domain.usecase.GetSpecificRoutineListUseCase
import com.forday.app.domain.usecase.ModifyPostingUseCase
import com.forday.app.domain.usecase.UploadImageToS3UseCase
import com.forday.app.domain.usecase.WriteRoutineUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RecordRoutineViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getSpecificRoutineListUseCase: GetSpecificRoutineListUseCase,
    private val writeRoutineUseCase: WriteRoutineUseCase,
    private val getPresignedUrlUseCase: GetPresignedUrlUseCase,
    private val uploadImageToS3UseCase: UploadImageToS3UseCase,
    private val modifyPostingUseCase: ModifyPostingUseCase,
    private val deleteS3ImageUseCase: DeleteS3ImageUseCase,
    private val getMyRoutineRecordDetailUseCase: GetMyRoutineRecordDetailUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<RecordRoutineSideEffect>() {

    private val _uiState: MutableStateFlow<RecordRoutineUiState> = MutableStateFlow(RecordRoutineUiState())
    val uiState: StateFlow<RecordRoutineUiState> = _uiState.toStateIn()

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: RecordRoutineAction) {
        when (action) {
            is RecordRoutineAction.FetchRoutineList -> fetchSpecificRoutineList(action.hobbyId, action.size)
            is RecordRoutineAction.LoadRecordDetail -> getMyRoutineRecordDetail(action.recordId)
            is RecordRoutineAction.WriteRoutine -> recordRoutine(
                action.routineId, action.sticker, action.memo,
                action.imageUrl, action.visibility, action.recordId
            )
            is RecordRoutineAction.ModifyPosting -> modifyPosting(
                action.recordId, action.routineId, action.sticker,
                action.memo, action.imageUrl, action.visibility
            )
            is RecordRoutineAction.GetPresignedUrl -> getPresignedUrl(action.images)
            is RecordRoutineAction.UploadImageToS3 -> uploadImageToS3(
                action.file, action.uploadUrl, action.contentType, action.order
            )
            is RecordRoutineAction.DeleteS3Image -> deleteS3Image(action.imageUrl)
        }
    }

    // ── Analytics (Screen에서 직접 호출) ─────────────────────────────
    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun recordRoutine(
        routineId: Long,
        sticker: String,
        memo: String,
        imageUrl: String,
        visibility: String,
        recordId: Int? = null
    ) = viewModelScope.launch {
        flow {
            emit(writeRoutineUseCase(routineId, sticker, memo, imageUrl, visibility))
        }.httpCatch(tag = "recordRoutine") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            val uiModel = data.data.toPresentation()
            _uiState.update {
                it.copy(
                    recordDetail = it.recordDetail.copy(
                        routineRecordId = recordId ?: uiModel.routineRecordId,
                        routineContent = uiModel.routineContent,
                        stickerUrl = uiModel.stickerUrl,
                        memo = uiModel.memo,
                        imageUrl = uiModel.imageUrl,
                        isExtensionRequired = uiModel.isExtensionRequired,
                        successMessage = uiModel.successMessage
                    )
                )
            }
            _sideEffectChannel.send(RecordRoutineSideEffect.WriteSuccess(uiModel.routineRecordId.toLong()))
        }
    }

    private fun modifyPosting(
        recordId: Int, routineId: Int, sticker: String,
        memo: String, imageUrl: String, visibility: String
    ) = viewModelScope.launch {
        flow {
            emit(modifyPostingUseCase(recordId, routineId, sticker, memo, imageUrl, visibility))
        }.httpCatch(tag = "modifyPosting") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            _sideEffectChannel.send(
                RecordRoutineSideEffect.ModifySuccess(
                    (_uiState.value.recordDetail.routineRecordId.takeIf { it > 0 }
                        ?: data.activityId).toLong()
                )
            )
        }
    }

    private fun getMyRoutineRecordDetail(recordId: Int) = viewModelScope.launch {
        flow {
            emit(getMyRoutineRecordDetailUseCase(recordId))
        }.httpCatch(tag = "getMyRoutineRecordDetail") { errorData ->
            _uiState.update { it.copy(errorData = errorData) }
        }.collect { data ->
            data?.let { detail ->
                _uiState.update { state ->
                    state.copy(
                        recordDetail = state.recordDetail.copy(
                            routineRecordId = detail.recordId,
                            routineContent = detail.content,
                            stickerUrl = detail.sticker,
                            memo = detail.memo,
                            imageUrl = detail.image
                        )
                    )
                }
            }
        }
    }

    private fun fetchSpecificRoutineList(hobbyId: Long?, size: Int?) = viewModelScope.launch {
        if (hobbyId == null) {
            Timber.w("fetchSpecificRoutineList: hobbyId is null")
            _uiState.update {
                it.copy(
                    errorData = ErrorDataUiState(
                        message = "잘못된 접근입니다.",
                        errorType = ErrorDataUiState.ErrorType.TYPE_BACK
                    )
                )
            }
            return@launch
        }

        flow {
            emit(getSpecificRoutineListUseCase(hobbyId, size))
        }.httpCatch(tag = "fetchSpecificRoutineList") { errorData ->
            _uiState.update { it.copy(errorData = errorData) }
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    recordDetail = state.recordDetail.copy(
                        routineList = data.data.routines.map { it.toUiModel() }
                    ),
                    errorData = null
                )
            }
        }
    }

    private fun getPresignedUrl(images: List<Map<String, Any>>) = viewModelScope.launch {
        flow {
            emit(getPresignedUrlUseCase(images).data)
        }.httpCatch(tag = "getPresignedUrl") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            _uiState.update { state ->
                state.copy(imageUploadState = data.toUiModel())
            }
        }
    }

    private fun deleteS3Image(imageUrl: String) = viewModelScope.launch {
        flow {
            emit(deleteS3ImageUseCase(imageUrl))
        }.httpCatch(tag = "deleteS3Image") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { }
    }

    private fun uploadImageToS3(
        file: File, uploadUrl: String, contentType: String, order: Int
    ) = viewModelScope.launch {
        flow {
            updateImageUploadStatus(order, isUploading = true)
            emit(uploadImageToS3UseCase(file, uploadUrl, contentType))
        }.catch { throwable ->
            updateImageUploadStatus(order, isUploading = false, isSuccess = false)
            snackbarManager.show(throwable.toUserMessage())
        }.collect {
            updateImageUploadStatus(order, isUploading = false, isSuccess = true)
        }
    }

    private fun updateImageUploadStatus(
        order: Int,
        isUploading: Boolean = false,
        isSuccess: Boolean = false
    ) {
        _uiState.update { state ->
            state.copy(
                imageUploadState = state.imageUploadState.copy(
                    images = state.imageUploadState.images.map { item ->
                        if (item.order == order) {
                            item.copy(isUploading = isUploading, isSuccess = isSuccess)
                        } else {
                            item
                        }
                    }
                )
            )
        }
    }
}
