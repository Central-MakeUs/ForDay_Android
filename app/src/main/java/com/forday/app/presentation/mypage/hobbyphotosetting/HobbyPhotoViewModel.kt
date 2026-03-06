package com.forday.app.presentation.mypage.hobbyphotosetting

import androidx.lifecycle.viewModelScope
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.GetPresignedUrlUseCase
import com.forday.app.domain.usecase.GetUserFeedListUseCase
import com.forday.app.domain.usecase.GetUsersProgressHobbyTabsUseCase
import com.forday.app.domain.usecase.SetHobbyMainImageUseCase
import com.forday.app.domain.usecase.UploadImageToS3UseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import com.forday.app.presentation.mypage.main.FeedContainerUiModel
import com.forday.app.presentation.mypage.main.toPresentation
import com.forday.app.presentation.mypage.routinedetail.toPresentation
import com.forday.app.presentation.mypage.toPresentation
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
class HobbyPhotoViewModel @Inject constructor(
    private val getUsersProgressHobbyTabsUseCase: GetUsersProgressHobbyTabsUseCase,
    private val getUserFeedListUseCase: GetUserFeedListUseCase,
    private val getPresignedUrlUseCase: GetPresignedUrlUseCase,
    private val uploadImageToS3UseCase: UploadImageToS3UseCase,
    private val setHobbyMainImageUseCase: SetHobbyMainImageUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<Unit>() {

    private val _uiState: MutableStateFlow<HobbyPhotoUiState> = MutableStateFlow(HobbyPhotoUiState())
    val uiState: StateFlow<HobbyPhotoUiState> = _uiState.toStateIn()

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: HobbyPhotoAction) {
        when (action) {
            is HobbyPhotoAction.LoadInitialData -> {
                getUsersProgressHobbyTabs()
                getUserFeedList(emptyList(), null, 100)
            }
            is HobbyPhotoAction.LoadFeedList -> getUserFeedList(
                action.hobbyIds, action.lastRecordId, action.feedSize
            )
            is HobbyPhotoAction.GetPresignedUrl -> getPresignedUrl(action.images)
            is HobbyPhotoAction.UploadImageToS3 -> uploadImageToS3(
                action.file, action.uploadUrl, action.contentType, action.order
            )
            is HobbyPhotoAction.SetHobbyMainImage -> setHobbyMainImage(
                action.hobbyId, action.imageUrl, action.recordId
            )
        }
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun getUsersProgressHobbyTabs() = viewModelScope.launch {
        flow {
            emit(getUsersProgressHobbyTabsUseCase(null))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    feed = state.feed.copy(hobbyTabs = data.toPresentation())
                )
            }
        }
    }

    private fun getUserFeedList(
        hobbyIds: List<Int?>,
        lastRecordId: Long?,
        feedSize: Long?,
    ) = viewModelScope.launch {
        flow {
            emit(getUserFeedListUseCase(hobbyIds, lastRecordId, feedSize, null))
        }.httpCatch(tag = "getUserFeedList") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            _uiState.update { state ->
                val currentFeedList = state.feed.feedContainer?.feedList ?: emptyList()
                val newFeedList = data.toPresentation().feedList

                val updatedFeedList = if (lastRecordId == null) {
                    newFeedList
                } else {
                    currentFeedList + newFeedList
                }

                val hasMore = newFeedList.isNotEmpty()

                state.copy(
                    feed = state.feed.copy(
                        feedContainer = FeedContainerUiModel(
                            feedList = updatedFeedList,
                            lastRecordId = data.lastRecordId,
                            totalFeedCount = data.totalFeedCount,
                            hasMore = hasMore
                        )
                    )
                )
            }
        }
    }

    private fun getPresignedUrl(images: List<Map<String, Any>>) = viewModelScope.launch {
        flow {
            emit(getPresignedUrlUseCase(images).data)
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    image = state.image.copy(uploadState = data.toPresentation())
                )
            }
        }
    }

    private fun uploadImageToS3(
        file: File,
        uploadUrl: String,
        contentType: String,
        order: Int
    ) = viewModelScope.launch {
        flow {
            updateImageUploadStatus(order, isUploading = true, isSuccess = false)
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
        isUploading: Boolean,
        isSuccess: Boolean
    ) {
        _uiState.update { state ->
            state.copy(
                image = state.image.copy(
                    uploadState = state.image.uploadState.copy(
                        isUploading = isUploading,
                        isSuccess = isSuccess
                    )
                )
            )
        }
    }

    private fun setHobbyMainImage(hobbyId: Long?, imageUrl: String? = null, recordId: Long?) = viewModelScope.launch {
        flow {
            emit(setHobbyMainImageUseCase(hobbyId, imageUrl, recordId))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    image = state.image.copy(mainImage = data.toPresentation())
                )
            }
        }
    }
}
