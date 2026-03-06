package com.forday.app.presentation.mypage.profilesetting

import androidx.lifecycle.viewModelScope
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.DeleteS3ImageUseCase
import com.forday.app.domain.usecase.GetIsNicknameDuplicateUseCase
import com.forday.app.domain.usecase.GetPresignedUrlUseCase
import com.forday.app.domain.usecase.RegisterNicknameUseCase
import com.forday.app.domain.usecase.SaveNicknameUseCase
import com.forday.app.domain.usecase.SetProfileImageUseCase
import com.forday.app.domain.usecase.UploadImageToS3UseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
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
class ProfileSettingViewModel @Inject constructor(
    private val getPresignedUrlUseCase: GetPresignedUrlUseCase,
    private val uploadImageToS3UseCase: UploadImageToS3UseCase,
    private val deleteS3ImageUseCase: DeleteS3ImageUseCase,
    private val setProfileImageUseCase: SetProfileImageUseCase,
    private val getIsNicknameDuplicateUseCase: GetIsNicknameDuplicateUseCase,
    private val registerNicknameUseCase: RegisterNicknameUseCase,
    private val saveNicknameUseCase: SaveNicknameUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<ProfileSettingSideEffect>() {

    private val _uiState: MutableStateFlow<ProfileSettingUiState> = MutableStateFlow(ProfileSettingUiState())
    val uiState: StateFlow<ProfileSettingUiState> = _uiState.toStateIn()

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: ProfileSettingAction) {
        when (action) {
            is ProfileSettingAction.GetPresignedUrl -> getPresignedUrl(action.images)
            is ProfileSettingAction.UploadImageToS3 -> uploadImageToS3(
                action.file, action.uploadUrl, action.contentType, action.order
            )
            is ProfileSettingAction.DeleteS3Image -> deleteS3Image(action.imageUrl)
            is ProfileSettingAction.SetProfileImage -> setProfileImage(action.imageUrl)
            is ProfileSettingAction.CheckNicknameDuplicate -> getIsNicknameDuplicate(action.nickname)
            is ProfileSettingAction.ResetNicknameCheck -> resetNicknameCheck()
            is ProfileSettingAction.RegisterNickname -> registerNickname(action.nickname)
        }
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun getPresignedUrl(images: List<Map<String, Any>>) = viewModelScope.launch {
        flow {
            emit(getPresignedUrlUseCase(images).data)
        }.catch { throwable ->
            Timber.e(throwable, "Failed to get presigned URL")
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

    private fun deleteS3Image(imageUrl: String) = viewModelScope.launch {
        flow {
            emit(deleteS3ImageUseCase(imageUrl))
        }.catch { throwable ->
            Timber.e(throwable, "deleteS3Image throwable")
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status == 404) snackbarManager.show(data.data.message)
        }
    }

    private fun setProfileImage(imageUrl: String? = null) = viewModelScope.launch {
        flow {
            emit(setProfileImageUseCase(imageUrl))
        }.catch { throwable ->
            Timber.e(throwable, "Failed to set profile image")
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status != 200) {
                snackbarManager.show(data.message)
            }
        }
    }

    private fun getIsNicknameDuplicate(nickName: String) = viewModelScope.launch {
        _uiState.update { state ->
            state.copy(
                nickname = state.nickname.copy(isCheckLoading = true)
            )
        }
        flow {
            emit(getIsNicknameDuplicateUseCase(nickName))
        }.catch { throwable ->
            _uiState.update { state ->
                state.copy(
                    nickname = state.nickname.copy(isCheckLoading = false)
                )
            }
            snackbarManager.show(throwable.toUserMessage())
        }.collect { result ->
            _uiState.update { state ->
                state.copy(
                    nickname = state.nickname.copy(
                        isCheckLoading = false,
                        checkMessage = result.data.message,
                        isChecked = result.data.available
                    )
                )
            }
        }
    }

    private fun resetNicknameCheck() {
        _uiState.update { state ->
            state.copy(
                nickname = state.nickname.copy(
                    checkMessage = "",
                    isChecked = null
                )
            )
        }
    }

    private fun registerNickname(nickName: String?) = viewModelScope.launch {
        flow {
            emit(registerNicknameUseCase(nickName))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.isSuccess) {
                nickName?.let { saveNicknameUseCase(it) }
                _sideEffectChannel.send(ProfileSettingSideEffect.NicknameRegisterSuccess)
            }
        }
    }
}
