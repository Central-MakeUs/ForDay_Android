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
import com.forday.app.domain.usecase.ModifyHobbyRoutineUseCase
import com.forday.app.domain.usecase.ModifyPostingUseCase
import com.forday.app.domain.usecase.UploadImageToS3UseCase
import com.forday.app.domain.usecase.WriteRoutineUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import com.forday.app.presentation.model.toModifyPostingUiModel
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
    private val modifyHobbyRoutineUseCase: ModifyHobbyRoutineUseCase,  // 수정용 취미활동
    private val getSpecificRoutineListUseCase: GetSpecificRoutineListUseCase,  // 특정 취미의 활동 목록 조회
    private val writeRoutineUseCase: WriteRoutineUseCase,
    private val getPresignedUrlUseCase: GetPresignedUrlUseCase,  // 이미지 업로드용 Presigned URL 발급
    private val uploadImageToS3UseCase: UploadImageToS3UseCase,
    private val modifyPostingUseCase: ModifyPostingUseCase,
    private val deleteS3ImageUseCase: DeleteS3ImageUseCase,   // S3에 등록된 이미지 삭제
    private val getMyRoutineRecordDetailUseCase: GetMyRoutineRecordDetailUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<Unit>() {

    private val _uiState: MutableStateFlow<RecordRoutineUiState> = MutableStateFlow(RecordRoutineUiState())
    val uiState: StateFlow<RecordRoutineUiState> = _uiState.toStateIn()

    fun recordRoutine(
        routineId: Long,
        sticker: String,
        memo: String,
        imageUrl: String,
        visibility: String,
        recordId: Int? = null,
        onSuccess: (Long) -> Unit
    ) = viewModelScope.launch {   // 취미 활동 기록
        flow {
            emit(writeRoutineUseCase(routineId, sticker, memo, imageUrl, visibility))
        }.httpCatch(tag = "recordRoutine") { errorData ->
            snackbarManager.show(errorData.message)
        }
            .collect { data ->
                val uiModel = data.data.toPresentation()
                _uiState.update {
                    it.copy(
                        recordDetail = it.recordDetail.copy(
                        routineRecordId = recordId ?: uiModel.routineRecordId,  //수정 모드 : recordId, 신규 : data.routineRecordId
                        routineContent = uiModel.routineContent,
                        stickerUrl = uiModel.stickerUrl,
                        memo = uiModel.memo,
                        imageUrl = uiModel.imageUrl,
                        isExtensionRequired = uiModel.isExtensionRequired,
                        successMessage = uiModel.successMessage
                    )
                )
            }

            // 콜백 - 작성한 게시글로 이동하기 위함
            onSuccess(uiModel.routineRecordId.toLong())
        }
    }

    fun modifyPosting(recordId: Int, routineId: Int, sticker: String, memo: String, imageUrl: String, visibility: String) // 활동 기록 수정
    = viewModelScope.launch {
        flow {
            emit(modifyPostingUseCase(recordId, routineId, sticker, memo, imageUrl, visibility))
        }.httpCatch(tag = "modifyPosting") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            _uiState.update {
                it.copy(
                    modifyPostingUiModel = data.toModifyPostingUiModel()
                )
            }
        }
    }

    fun resetModifyPostingUiModel() {
        _uiState.update { state ->
            state.copy(modifyPostingUiModel = null)
        }
    }

    fun getMyRoutineRecordDetail(recordId: Int) = viewModelScope.launch {  // 수정 모드. 수정할 게시글 데이터 불러오기
        if (recordId == null) {
            Timber.w("getMyRoutineRecordDetail: recordId is null")
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
            emit(getMyRoutineRecordDetailUseCase(recordId))
        }.httpCatch(tag = "getMyRoutineRecordDetail") { errorData ->
            _uiState.update {
                it.copy(
                    errorData = errorData
                )
            }
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

    fun fetchSpecificRoutineList(hobbyId: Long?, size: Int? = null) = viewModelScope.launch {  // 드롭 다운용 특정 취미 활동 목록 조회
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
            _uiState.update {
                it.copy(
                    errorData = errorData
                )
            }
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    recordDetail = state.recordDetail.copy(
                        routineList = data.data.routines.map { it.toUiModel() }
                    ),
                    errorData = null
                )
            }
        }
    }

    fun getPresignedUrl(images: List<Map<String, Any>>) = viewModelScope.launch {  // 이미지 업로드용 Presigned URL 발급
        flow {
            emit(getPresignedUrlUseCase(images).data)
        }.httpCatch(tag = "getPresignedUrl") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    imageUploadState = data.toUiModel()
                )
            }
        }
    }

    fun deleteS3Image(imageUrl: String) = viewModelScope.launch {  // S3에 업로드한 이미지 삭제
        flow {
            emit(deleteS3ImageUseCase(imageUrl))
        }.httpCatch(tag = "deleteS3Image") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { }
    }


    fun uploadImageToS3(     // S3에 이미지 업로드 TODO 에러 핸들링 재수정 해야 함
        file: File,
        uploadUrl: String,
        contentType: String,
        order: Int
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

    private fun updateImageUploadStatus(   // 특정 이미지의 업로드 상태 업데이트
        order: Int,
        isUploading: Boolean = false,
        isSuccess: Boolean = false
    ) {
        _uiState.update { state ->
            state.copy(
                imageUploadState = state.imageUploadState.copy(
                    images = state.imageUploadState.images.map { item ->
                        if (item.order == order) {
                            item.copy(
                                isUploading = isUploading,
                                isSuccess = isSuccess
                            )
                        } else {
                            item
                        }
                    }
                )
            )
        }
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

}