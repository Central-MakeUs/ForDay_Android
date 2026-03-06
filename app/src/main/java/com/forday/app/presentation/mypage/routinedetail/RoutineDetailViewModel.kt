package com.forday.app.presentation.mypage.routinedetail

import androidx.lifecycle.viewModelScope
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.CancelMyReactionUseCase
import com.forday.app.domain.usecase.CancelScrapPostingUseCase
import com.forday.app.domain.usecase.DeletePostingUseCase
import com.forday.app.domain.usecase.GetMyRoutineRecordDetailUseCase
import com.forday.app.domain.usecase.GetReactionUsersUseCase
import com.forday.app.domain.usecase.GetUserNicknameUseCase
import com.forday.app.domain.usecase.ModifyPostingVisibilityUseCase
import com.forday.app.domain.usecase.ReactionToRoutinePostingUseCase
import com.forday.app.domain.usecase.ScrapPostingUseCase
import com.forday.app.domain.usecase.SetHobbyMainImageUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.mypage.routinedetail.screen.ReactionDetailUiModel
import com.forday.app.presentation.mypage.routinedetail.screen.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    private val getMyRoutineRecordDetailUseCase: GetMyRoutineRecordDetailUseCase,
    private val reactionToRoutinePostingUseCase: ReactionToRoutinePostingUseCase,
    private val cancelMyReactionUseCase: CancelMyReactionUseCase,
    private val modifyPostingVisibilityUseCase: ModifyPostingVisibilityUseCase,
    private val getReactionUsersUseCase: GetReactionUsersUseCase,
    private val deletePostingUseCase: DeletePostingUseCase,
    private val scrapPostingUseCase: ScrapPostingUseCase,
    private val cancelScrapPostingUseCase: CancelScrapPostingUseCase,
    private val setHobbyMainImageUseCase: SetHobbyMainImageUseCase,
    private val getNicknameUseCase: GetUserNicknameUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<RoutineDetailSideEffect>() {

    private val _uiState: MutableStateFlow<RoutineDetailUiState> = MutableStateFlow(RoutineDetailUiState())
    val uiState: StateFlow<RoutineDetailUiState> = _uiState.toStateIn()

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: RoutineDetailAction) {
        when (action) {
            is RoutineDetailAction.LoadDetail -> {
                getMyRoutineRecordDetail(action.routineId)
                getNickname()
            }
            is RoutineDetailAction.ToggleScrap -> {
                if (action.isCurrentlyScraped) {
                    cancelScrapPosting(action.routineId)
                } else {
                    scrapPosting(action.routineId)
                }
            }
            is RoutineDetailAction.ReactToPosting -> reactionToRoutinePosting(
                action.routineId, action.reactionType, action.refreshUsers
            )
            is RoutineDetailAction.CancelReaction -> cancelMyReaction(
                action.routineId, action.reactionType, action.refreshUsers
            )
            is RoutineDetailAction.GetReactionUsers -> getReactionUsers(
                action.routineId, action.reactionType, "", 10
            )
            is RoutineDetailAction.SetHobbyMainImage -> setHobbyMainImage(
                action.hobbyId, null, action.recordId
            )
            is RoutineDetailAction.DeletePosting -> deletePosting(action.recordId)
            is RoutineDetailAction.ModifyVisibility -> modifyPostingVisibility(
                action.recordId, action.visibility
            )
        }
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun getMyRoutineRecordDetail(routineId: Int) = viewModelScope.launch {
        flow {
            emit(getMyRoutineRecordDetailUseCase(routineId))
        }.catch { throwable ->
            Timber.e("getMyRoutineRecordDetail error: $throwable")
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    detail = state.detail.copy(routine = data?.toPresentation())
                )
            }
        }
    }

    private fun reactionToRoutinePosting(recordId: Int, reactionType: String, refreshUsers: Boolean) = viewModelScope.launch {
        flow {
            emit(reactionToRoutinePostingUseCase(recordId, reactionType))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status != 200) snackbarManager.show(data.message)
            else if (refreshUsers) getReactionUsers(recordId, reactionType, "", 10)
        }
    }

    private fun cancelMyReaction(recordId: Int, reactionType: String, refreshUsers: Boolean) = viewModelScope.launch {
        flow {
            emit(cancelMyReactionUseCase(recordId, reactionType))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status != 200) snackbarManager.show(data.message)
            else if (refreshUsers) getReactionUsers(recordId, reactionType, "", 10)
        }
    }

    private fun modifyPostingVisibility(recordId: Int, visibility: String) = viewModelScope.launch {
        flow {
            emit(modifyPostingVisibilityUseCase(recordId, visibility))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status != 200) snackbarManager.show(data.message)
        }
    }

    private fun getReactionUsers(recordId: Int, reactionType: String, lastUserId: String, size: Int) = viewModelScope.launch {
        _uiState.update { state ->
            state.copy(
                reaction = state.reaction.copy(
                    users = ReactionDetailUiModel(
                        reactionType = reactionType,
                        users = emptyList()
                    )
                )
            )
        }
        flow {
            emit(getReactionUsersUseCase(recordId, reactionType, lastUserId, size))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            if (data.status != 200) snackbarManager.show(data.message)
            _uiState.update { state ->
                state.copy(
                    reaction = state.reaction.copy(users = data.toUiModel())
                )
            }
        }
    }

    private fun deletePosting(recordId: Long) = viewModelScope.launch {
        flow {
            emit(deletePostingUseCase(recordId))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { domainData ->
            snackbarManager.show(domainData.message)
            _sideEffectChannel.send(RoutineDetailSideEffect.DeleteSuccess)
        }
    }

    private fun scrapPosting(routineId: Int) = viewModelScope.launch {
        flow {
            emit(scrapPostingUseCase(routineId))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update {
                it.copy(
                    reaction = it.reaction.copy(isScraped = data.data.scraped)
                )
            }
            snackbarManager.show(data.data.message)
        }
    }

    private fun cancelScrapPosting(routineId: Int) = viewModelScope.launch {
        flow {
            emit(cancelScrapPostingUseCase(routineId))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update {
                it.copy(
                    reaction = it.reaction.copy(isScraped = data.isScraped)
                )
            }
        }
    }

    private fun setHobbyMainImage(hobbyId: Long?, imageUrl: String? = null, recordId: Long?) = viewModelScope.launch {
        flow {
            emit(setHobbyMainImageUseCase(hobbyId, imageUrl, recordId))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { /* 대표사진 설정 완료 - 토스트는 Screen에서 처리 */ }
    }

    private fun getNickname() = viewModelScope.launch {
        getNicknameUseCase()
            .catch { throwable ->
                snackbarManager.show(throwable.toUserMessage())
            }
            .collect { nickname ->
                _uiState.update { state ->
                    state.copy(
                        detail = state.detail.copy(nickname = nickname)
                    )
                }
            }
    }
}
