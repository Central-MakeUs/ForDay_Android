package com.forday.app.presentation.sosik

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.CancelMyReactionUseCase
import com.forday.app.domain.usecase.GetMyRoutineRecordDetailUseCase
import com.forday.app.domain.usecase.GetPeopleRoutineListUseCase
import com.forday.app.domain.usecase.ReactionToRoutinePostingUseCase
import com.forday.app.domain.usecase.SwitchAccountUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import com.forday.app.presentation.mypage.MyPageSideEffect
import com.forday.app.presentation.sosik.model.SosikContentUiState
import com.forday.app.presentation.sosik.model.SosikUiState
import com.forday.app.presentation.sosik.model.toPresentation
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SosikViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val snackbarManager: SnackbarManager,
    private val getPeopleRoutineListUseCase: GetPeopleRoutineListUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val userLocalDataSource: UserLocalDataSource,
    private val getMyRoutineRecordDetailUseCase: GetMyRoutineRecordDetailUseCase,
    private val reactionToRoutinePostingUseCase: ReactionToRoutinePostingUseCase,
    private val cancelMyReactionUseCase: CancelMyReactionUseCase,
) : BaseViewModel<SosikSideEffect>() {

    private val _uiState = MutableStateFlow(SosikUiState())
    val uiState: StateFlow<SosikUiState> = _uiState.toStateIn()

    fun fetchPeopleRoutineList(
        hobbyId: Long? = null,   // 취미 ID (null이면 가장 최근 취미로 조회)
        lastRecordId: Long? = null,  // 마지막으로 조회된 기록 ID (null이면 처음부터 조회)
        size: Long? = 20L,  // 기본 20
        keyword: String? = null,   // 검색 키워드
        storyFilterType: String? = "ALL"  // 기본 ALL
    ) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        Timber.e("@@@@@@@#########data  호출 전"+hobbyId+", "+lastRecordId+", "+size+", "+keyword+", "+storyFilterType)
        flow {
            emit(getPeopleRoutineListUseCase(hobbyId, lastRecordId, size, keyword, storyFilterType))
        }.httpCatch(tag = "fetchPeopleRoutineList") { errorData ->
            Timber.e("@@@@@@@#########data  errorData "+errorData)
            _uiState.update { it.copy(isLoading = false, errorData = errorData) }
        }.collect { data ->
            Timber.e("@@@@@@@#########data "+data)
            val tabList = data.data?.tabInfo?.map { it.toPresentation() } ?: emptyList()
            val recordList = data.data?.recordList?.map { it.toPresentation() }
                ?.distinctBy { it.recordId }
                ?: emptyList()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    content = SosikContentUiState(
                        tabList = tabList,
                        recordList = recordList,
                        hasNext = data.data?.hasNext ?: false,
                        lastRecordId = data.data?.lastRecordId
                    )
                )
            }
        }
    }

    fun loadMorePeopleRoutineList() {
        val current = _uiState.value
        if (!current.content.hasNext || current.isLoadingMore) return

        val selectedHobbyId = if (current.selectedTabIndex == 0) null
            else current.content.tabList.getOrNull(current.selectedTabIndex - 1)?.hobbyId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            flow {
                emit(getPeopleRoutineListUseCase(selectedHobbyId, current.content.lastRecordId, 20L, null, "ALL"))
            }.httpCatch(tag = "loadMorePeopleRoutineList") { errorData ->
                _uiState.update { it.copy(isLoadingMore = false, errorData = errorData) }
            }.collect { data ->
                val newRecords = data.data?.recordList?.map { it.toPresentation() } ?: emptyList()
                _uiState.update { state ->
                    state.copy(
                        isLoadingMore = false,
                        content = state.content.copy(
                            recordList = (state.content.recordList + newRecords).distinctBy { it.recordId },
                            hasNext = data.data?.hasNext ?: false,
                            lastRecordId = data.data?.lastRecordId
                        )
                    )
                }
            }
        }
    }

    fun selectTab(index: Int) {
        val selectedHobbyId = if (index == 0) null
            else _uiState.value.content.tabList.getOrNull(index - 1)?.hobbyId
        _uiState.update { it.copy(selectedTabIndex = index) }
        fetchPeopleRoutineList(hobbyId = selectedHobbyId)
    }

    fun toggleAwesome(recordId: Long) {
        _uiState.update { state ->
            state.copy(
                content = state.content.copy(
                    recordList = state.content.recordList.map { record ->
                        if (record.recordId == recordId) record.copy(pressedAweSome = !record.pressedAweSome)
                        else record
                    }
                )
            )
        }
    }

    fun getUserLoginInfo() = viewModelScope.launch {
        userLocalDataSource.getSocialType().collect { socialType ->
            _uiState.update { it.copy(socialType = socialType) }
        }
    }

    fun markGuestBottomSheetShown() {
        _uiState.update { it.copy(hasShownGuestBottomSheet = true) }
    }

    fun loginWithKakao(context: Context, socialType: String) {
        val kakao = UserApiClient.instance
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null || token == null) {
                val errorMessage = error?.toUserMessage(UserMessageCategory.AUTH)
                    ?: "로그인에 실패했어요. 잠시 후 다시 시도해주세요."
                viewModelScope.launch { snackbarManager.show(errorMessage) }
            } else {
                loginIntoApp(socialType, token.accessToken)
            }
        }
        if (kakao.isKakaoTalkLoginAvailable(context)) {
            kakao.loginWithKakaoTalk(context) { token, error ->
                if (error != null || token == null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        viewModelScope.launch {
                            snackbarManager.show(error.toUserMessage(UserMessageCategory.AUTH))
                        }
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else {
                    loginIntoApp(socialType, token.accessToken)
                }
            }
        } else {
            kakao.loginWithKakaoAccount(context, callback = callback)
        }
    }

    private fun loginIntoApp(socialType: String, kakaoAccessToken: String) = viewModelScope.launch {
        switchAccountUseCase(socialType, kakaoAccessToken)
            .onSuccess { data ->
                _uiState.update { it.copy(socialType = data.socialType) }
            }
            .onFailure { error ->
                val errorMessage = error.toUserMessage(UserMessageCategory.AUTH)
                viewModelScope.launch { snackbarManager.show(errorMessage) }
            }
    }

    fun getRoutineRecordDetail(recordId: Int) = viewModelScope.launch {
        flow {
            emit(getMyRoutineRecordDetailUseCase(recordId))
        }.httpCatch(tag = "getRoutineRecordDetail") { errorData ->
            _uiState.update { it.copy(isLoading = false, errorData = errorData) }
        }.collect { data ->
            _uiState.update { it.copy(isLoading = false, routineDetail = data) }
        }
    }

    fun reactionToPosting(recordId: Int, reactionType: String) = viewModelScope.launch {
        flow {
            emit(reactionToRoutinePostingUseCase(recordId, reactionType))
        }.httpCatch(tag = "reactionToPosting") { errorData ->
            _uiState.update { it.copy(errorData = errorData) }
        }.collect { }
    }

    fun cancelReaction(recordId: Int, reactionType: String) = viewModelScope.launch {
        flow {
            emit(cancelMyReactionUseCase(recordId, reactionType))
        }.httpCatch(tag = "cancelReaction") { errorData ->
            _uiState.update { it.copy(errorData = errorData) }
        }.collect { }
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }
}
