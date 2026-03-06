package com.forday.app.presentation.mypage

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.domain.usecase.BlockUserUseCase
import com.forday.app.domain.usecase.GetUserFeedListUseCase
import com.forday.app.domain.usecase.GetUserInfoUseCase
import com.forday.app.domain.usecase.GetUserNicknameUseCase
import com.forday.app.domain.usecase.GetUserScrapListUseCase
import com.forday.app.domain.usecase.GetUsersProgressHobbyTabsUseCase
import com.forday.app.domain.usecase.SwitchAccountUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import com.forday.app.presentation.mypage.main.FeedContainerUiModel
import com.forday.app.presentation.mypage.main.UserInfoUiModel
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject
import com.forday.app.presentation.mypage.main.toPresentation
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getUsersProgressHobbyTabsUseCase: GetUsersProgressHobbyTabsUseCase,
    private val getUserFeedListUseCase: GetUserFeedListUseCase,
    private val getNicknameUseCase: GetUserNicknameUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val getUserScrapListUseCase: GetUserScrapListUseCase,
    private val blockUserUseCase: BlockUserUseCase,
    private val getUserData: UserLocalDataSource,
    private val snackbarManager: SnackbarManager,
): BaseViewModel<MyPageSideEffect>() {

    private val _uiState: MutableStateFlow<MyPageUiState> = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState.toStateIn()

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: MyPageAction) {
        when (action) {
            is MyPageAction.LoadInitialData -> {
                getUserInfo(action.userId)
                getUserLoginInfo()
                getUsersProgressHobbyTabs(action.userId)
                getUserFeedList(
                    hobbyIds = emptyList(),
                    lastRecordId = null,
                    feedSize = 24,
                    userId = action.userId
                )
            }
            is MyPageAction.Refresh -> refresh(
                action.selectedTab, action.selectedHobbyIds, action.userId
            )
            is MyPageAction.SelectTab -> {
                if (action.tab == 2) {
                    getUserScrapList(lastScrapId = null, size = 24, userId = action.userId)
                }
            }
            is MyPageAction.FilterByHobby -> getUserFeedList(
                hobbyIds = action.hobbyIds,
                lastRecordId = null,
                feedSize = 24,
                userId = action.userId
            )
            is MyPageAction.MarkGuestBottomSheetShown -> {
                _uiState.update { it.copy(hasShownGuestBottomSheet = true) }
            }
            is MyPageAction.LoginWithKakao -> loginWithKakao(action.context, action.socialType)
            is MyPageAction.BlockUser -> blockUser(action.userId, action.nickname)
            is MyPageAction.ResetBlockUserSuccess -> {
                _uiState.update { it.copy(blockUserSuccess = false) }
            }
        }
    }

    // ── 데이터 로드 (ProfileSetting goBack 시에도 호출) ──────────────
    fun getUserInfo(userId: String? = null) = viewModelScope.launch {
        flow {
            emit(getUserInfoUseCase(userId))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    profile = state.profile.copy(
                        userInfo = UserInfoUiModel(
                            profileImageUrl = data.imageUrl,
                            nickName = data.nickname,
                            totalCollectedStickerCount = data.stickerCount
                        )
                    )
                )
            }
        }
    }

    fun getNickname() = viewModelScope.launch {
        getNicknameUseCase()
            .catch { throwable ->
                snackbarManager.show(throwable.toUserMessage())
            }
            .collect { nickname ->
                _uiState.update { state ->
                    state.copy(
                        profile = state.profile.copy(
                            userInfo = (state.profile.userInfo ?: UserInfoUiModel()).copy(
                                nickName = nickname
                            )
                        )
                    )
                }
            }
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun refresh(selectedTab: Int, selectedHobbyIds: List<Int?>, userId: String?) = viewModelScope.launch {
        _uiState.update { it.copy(isRefreshing = true) }
        try {
            withTimeout(4_000L) {
                val jobs = mutableListOf(
                    launch { getUserInfo(userId).join() },
                    launch { getUsersProgressHobbyTabs(userId).join() },
                    launch {
                        getUserFeedList(
                            hobbyIds = selectedHobbyIds,
                            lastRecordId = null,
                            feedSize = 24,
                            userId = userId
                        ).join()
                    }
                )
                if (selectedTab == 2) {
                    jobs += launch {
                        getUserScrapList(lastScrapId = null, size = 24, userId = null).join()
                    }
                }
                jobs.forEach { it.join() }
            }
        } catch (_: TimeoutCancellationException) {
            Timber.d("MyPage refresh timed out after 4s")
        } finally {
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun getUsersProgressHobbyTabs(userId: String? = null) = viewModelScope.launch {
        flow {
            emit(getUsersProgressHobbyTabsUseCase(userId))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update { state ->
                state.copy(
                    profile = state.profile.copy(
                        hobbyTabs = data.toPresentation()
                    )
                )
            }
        }
    }

    private fun getUserFeedList(
        hobbyIds: List<Int?>,
        lastRecordId: Long?,
        feedSize: Long?,
        userId: String? = null
    ) = viewModelScope.launch {
        flow {
            emit(getUserFeedListUseCase(hobbyIds, lastRecordId, feedSize, userId))
        }.httpCatch(tag = "getUserFeedList") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            Timber.d("getUserFeedList Success: ${data.toPresentation().feedList.size}개 조회")

            _uiState.update { state ->
                val currentFeedList = state.feed.container?.feedList ?: emptyList()
                val newFeedList = data.toPresentation().feedList

                val updatedFeedList = if (lastRecordId == null) {
                    newFeedList
                } else {
                    currentFeedList + newFeedList
                }

                val hasMore = newFeedList.isNotEmpty()

                Timber.d("Total feedList size: ${updatedFeedList.size}")

                state.copy(
                    feed = state.feed.copy(
                        container = FeedContainerUiModel(
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

    private fun getUserScrapList(lastScrapId: Long?, size: Long?, userId: String?) = viewModelScope.launch {
        flow {
            emit(getUserScrapListUseCase(lastScrapId, size, userId))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage())
        }.collect { data ->
            _uiState.update {
                it.copy(
                    scrap = it.scrap.copy(
                        list = data.data.toPresentation()
                    )
                )
            }
        }
    }

    private fun blockUser(userId: String, nickname: String) = viewModelScope.launch {
        flow {
            emit(blockUserUseCase(userId))
        }.httpCatch(tag = "blockUser") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            if (data.success) {
                val displayNickname = if (nickname.length > 10) nickname.take(10) else nickname
                snackbarManager.show("${displayNickname} 님이 차단되었어요.")
                _uiState.update { it.copy(blockUserSuccess = true, isBlockedUser = true) }
            } else {
                snackbarManager.show(data.data.message)
            }
        }
    }

    private fun getUserLoginInfo() = viewModelScope.launch {
        getUserData.getSocialType().collect { socialType ->
            _uiState.update { state ->
                state.copy(
                    auth = state.auth.copy(socialType = socialType)
                )
            }
        }
    }

    private fun loginWithKakao(context: Context, socialType: String) {
        val kakao = UserApiClient.instance
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null || token == null) {
                val errorMessage = error?.toUserMessage(UserMessageCategory.AUTH)
                    ?: "로그인에 실패했어요. 잠시 후 다시 시도해주세요."
                sendSideEffect(MyPageSideEffect.DomainError(errorMessage))
            } else {
                loginIntoApp(socialType, token.accessToken)
            }
        }

        if (kakao.isKakaoTalkLoginAvailable(context)) {
            kakao.loginWithKakaoTalk(context) { token, error ->
                if (error != null || token == null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        sendSideEffect(MyPageSideEffect.DomainError(error.toUserMessage(UserMessageCategory.AUTH)))
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

    private fun sendSideEffect(sideEffect: MyPageSideEffect) = viewModelScope.launch {
        when (sideEffect) {
            is MyPageSideEffect.DomainError -> snackbarManager.show(sideEffect.error)
            is MyPageSideEffect.Exception -> snackbarManager.show(sideEffect.error.toUserMessage())
        }
    }

    private fun loginIntoApp(socialType: String, kakaoAccessToken: String) = viewModelScope.launch {
        try {
            switchAccountUseCase(socialType, kakaoAccessToken)
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            auth = it.auth.copy(
                                isKakaoLoginSuccess = true,
                                socialType = data.socialType
                            )
                        )
                    }
                }
                .onFailure { error ->
                    val errorMessage = error.toUserMessage(UserMessageCategory.AUTH)
                    _uiState.update {
                        it.copy(
                            auth = it.auth.copy(isKakaoLoginSuccess = false)
                        )
                    }
                    sendSideEffect(MyPageSideEffect.DomainError(errorMessage))
                }
        } catch (e: Exception) {
            val errorMessage = e.toUserMessage(UserMessageCategory.AUTH)
            _uiState.update {
                it.copy(
                    auth = it.auth.copy(isKakaoLoginSuccess = false)
                )
            }
            sendSideEffect(MyPageSideEffect.DomainError(errorMessage))
        }
    }
}
