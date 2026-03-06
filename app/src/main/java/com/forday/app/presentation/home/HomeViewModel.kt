package com.forday.app.presentation.home

import androidx.lifecycle.viewModelScope
import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsEvents
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.core.util.logAndExtractServerMessage
import com.forday.app.core.util.toUserMessage
import com.forday.app.domain.usecase.CreateRoutinesUseCase
import com.forday.app.domain.usecase.GetAiRecommendedRoutinesAgainUseCase
import com.forday.app.domain.usecase.GetAiRecommendedRoutinesUseCase
import com.forday.app.domain.usecase.GetHomeHobbyUseCase
import com.forday.app.domain.usecase.GetMyHobbyListUseCase
import com.forday.app.domain.usecase.GetSpecificRoutineListUseCase
import com.forday.app.domain.usecase.GetStickersUseCase
import com.forday.app.domain.usecase.GetUserNicknameUseCase
import com.forday.app.domain.usecase.WriteRoutineUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import com.forday.app.presentation.home.model.AiRoutineItemState
import com.forday.app.presentation.home.model.HomeState
import com.forday.app.presentation.home.model.RoutinePreviewUiModel
import com.forday.app.presentation.home.model.RoutineUiModel
import com.forday.app.presentation.home.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getHomeHobbyUseCase: GetHomeHobbyUseCase,
    private val getSpecificRoutineListUseCase: GetSpecificRoutineListUseCase,
    private val getStickersUseCase: GetStickersUseCase,
    private val getAiRecommendedRoutinesUseCase: GetAiRecommendedRoutinesUseCase,
    private val getAiRecommendedRoutinesAgainUseCase: GetAiRecommendedRoutinesAgainUseCase,
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val createRoutinesUseCase: CreateRoutinesUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<HomeSideEffect>() {

    private val _uiState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.toStateIn()

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.LoadHomeData -> fetchHomeHobbyData(action.hobbyId)
            is HomeAction.SelectHobby -> handleSelectHobby(action.hobbyId)
            is HomeAction.SelectRoutine -> selectRoutine(action.routineId)
            is HomeAction.NextStickerPage -> nextStickerPage()
            is HomeAction.PreviousStickerPage -> previousStickerPage()
            is HomeAction.RequestAiRecommendation -> getAiRecommendedRoutines(action.hobbyId)
            is HomeAction.RequestAiRecommendationAgain -> getAiRecommendedRoutinesAgain(action.hobbyId, action.type)
            is HomeAction.CreateRoutines -> createRoutines(action.hobbyId, action.routineList, action.hobbyName)
            is HomeAction.RefreshAfterAiDismiss -> fetchHomeHobbyData(action.hobbyId)
            is HomeAction.LogEvent -> analyticsManager.logEvent(action.event)
            is HomeAction.LogAnalyticsEvent -> analyticsManager.logEvent(action.event)
        }
    }

    // 취미 선택 시 홈 데이터 + 루틴 + 스티커 + 닉네임 일괄 로드
    private fun handleSelectHobby(hobbyId: Long?) {
        fetchHomeHobbyData(hobbyId)
        if (hobbyId != null) {
            fetchSpecificRoutineList(hobbyId, 5)
            fetchStickerHistory(hobbyId, 28, null)
        }
        getUserNickname()
    }

    fun fetchHomeHobbyData(hobbyId: Long? = null) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        flow {
            val domain = getHomeHobbyUseCase(hobbyId)
            val uiModel = domain.data?.toPresentation()
            emit(uiModel)
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("HomeHobbyData Fetch Error: $throwable")
            _uiState.update { it.copy(isLoading = false) }
            val message = when (throwable) {
                is HttpException -> throwable.logAndExtractServerMessage(tag = "fetchHomeHobbyData")
                else -> null
            }
            snackbarManager.show(message ?: throwable.toUserMessage())
        }.collect { data ->
            data?.let { uiModel ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        hobby = state.hobby.copy(
                            inProgressHobbies = uiModel.inProgressHobbies,
                            greetingMessage = uiModel.greetingMessage,
                            userSummaryText = uiModel.userSummaryText,
                            recommendMessage = uiModel.recommendMessage,
                        ),
                        routine = state.routine.copy(
                            preview = uiModel.routinePreview,
                            aiCallRemaining = uiModel.aiCallRemaining,
                            aiCallRemainingCount = uiModel.aiCallRemainingCount,
                        ),
                    )
                }
            } ?: _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun fetchSpecificRoutineList(hobbyId: Long?, size: Int?) =
        viewModelScope.launch {
            flow {
                val response = getSpecificRoutineListUseCase(hobbyId, size)
                emit(response.data.routines)
            }.httpCatch(tag = "fetchSpecificRoutineList") { errorData ->

            }.collect { routines ->
                val routineUiModels = routines.map { item ->
                    RoutineUiModel(
                        routineId = item.routineId,
                        content = item.content,
                        isAiRecommended = item.aiRecommended,
                    )
                }

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        routine = state.routine.copy(list = routineUiModels),
                    )
                }
            }
        }


    fun fetchStickerHistory(hobbyId: Long?, size: Int = 28, page: Int? = null) = viewModelScope.launch {
        flow {
            val response = getStickersUseCase(hobbyId, page, size)
            emit(response)
        }.httpCatch(tag = "fetchStickerHistory") { errorData ->

        }.collect { result ->
            val stickerInfo = result.toPresentation()
            val stickerList = result.stickers.map { it.toPresentation() }

            _uiState.update { currentState ->
                currentState.copy(
                    sticker = currentState.sticker.copy(
                        info = stickerInfo,
                        stickers = stickerList,
                    ),
                )
            }
        }
    }

    private fun selectRoutine(routineId: Int) {
        val selectedRoutine = _uiState.value.routineList.find { it.routineId == routineId }
        selectedRoutine?.let { routine ->
            _uiState.update { state ->
                state.copy(
                    routine = state.routine.copy(
                        preview = RoutinePreviewUiModel(
                            routineId = routine.routineId,
                            content = routine.content,
                            isAiRecommended = routine.isAiRecommended
                        )
                    ),
                )
            }
        }
    }

    private fun nextStickerPage() {
        val currentHobbyId = uiState.value.inProgressHobbies.find { it.isCurrent }?.hobbyId
        val currentApiPage = uiState.value.stickerInfo?.currentPage ?: 0
        fetchStickerHistory(hobbyId = currentHobbyId, size = 28, page = currentApiPage + 1)
    }

    private fun previousStickerPage() {
        val currentHobbyId = uiState.value.inProgressHobbies.find { it.isCurrent }?.hobbyId
        val currentApiPage = uiState.value.stickerInfo?.currentPage ?: 1
        fetchStickerHistory(hobbyId = currentHobbyId, size = 28, page = currentApiPage - 1)
    }

    private fun getAiRecommendedRoutines(hobbyId: Long?) = viewModelScope.launch {
        flow {
            emit(getAiRecommendedRoutinesUseCase(hobbyId))
        }.httpCatch(tag = "getAiRecommendedRoutines") { errorData ->
            when (errorData.errorClassName) {
                "AI_CALL_LIMIT_EXCEEDED" -> getAiRecommendedRoutinesAgain(hobbyId)
                else -> _uiState.update { it.copy(isLoading = false, errorData = errorData) }
            }
        }.collect { result ->
            val newAiRoutines = result.data.routines.map { it.toPresentation() }
            _uiState.update {
                it.copy(
                    aiRecommend = it.aiRecommend.copy(
                        routines = newAiRoutines,
                        loaded = !it.aiRecommend.loaded,
                        callCount = result.data.aiCallCount,
                        recommendedText = result.data.recommendedText,
                    ),
                    isLoading = false,
                    errorData = null,
                )
            }
        }
    }

    private fun getAiRecommendedRoutinesAgain(hobbyId: Long?, type: String? = "ALL") = viewModelScope.launch {
        flow {
            emit(getAiRecommendedRoutinesAgainUseCase(hobbyId, type))
        }.httpCatch(tag = "getAiRecommendedRoutinesAgain") { errorData ->
            _uiState.update { it.copy(isLoading = false, errorData = errorData) }
        }.collect { result ->
            val aiRoutines = result.data.activityItems.map { item ->
                AiRoutineItemState(
                    routineId = item.itemId,
                    topic = "",
                    content = item.content,
                    description = item.description
                )
            }
            _uiState.update {
                it.copy(
                    aiRecommend = it.aiRecommend.copy(
                        routines = aiRoutines,
                        loaded = !it.aiRecommend.loaded,
                        recommendedText = result.data.message,
                    ),
                    isLoading = false,
                    errorData = null,
                )
            }
        }
    }

    private fun createRoutines(hobbyId: Long?, routineList: List<Pair<Boolean, String>>, hobbyName: String? = null) =
        viewModelScope.launch {
            flow {
                emit(createRoutinesUseCase.invoke(hobbyId, routineList))
            }.httpCatch(tag = "createRoutines") { errorData ->
                when (errorData.errorClassName) {
                    "VALIDATION_ERROR" -> _sideEffectChannel.send(HomeSideEffect.ShowErrorToast(errorData.message))
                }
            }.collect { result ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        routine = it.routine.copy(createdRoutineId = result.data.createdRoutineNum),
                    )
                }
                routineList.forEach { (isAi, activityName) ->
                    analyticsManager.logEvent(
                        AnalyticsEvents.activityAdded(
                            entryPoint = "ai_banner",
                            source = if (isAi) "ai_recommendation" else "manual",
                            hobbyName = hobbyName,
                            activityName = activityName
                        )
                    )
                }
                _sideEffectChannel.send(HomeSideEffect.ShowToast("AI 취미활동을 담았어요."))
            }
        }

    fun getUserNickname() = viewModelScope.launch {
        getUserNicknameUseCase()
            .catch { throwable ->
                throwable.printStackTrace()
                val message = when (throwable) {
                    is HttpException -> throwable.logAndExtractServerMessage(tag = "getUserNickname")
                    else -> null
                }
                snackbarManager.show(message ?: throwable.toUserMessage())
            }.collect { data ->
                _uiState.update { state ->
                    state.copy(nickName = data)
                }
            }
    }
}
