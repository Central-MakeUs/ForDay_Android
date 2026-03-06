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
import kotlinx.coroutines.async
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
    private val getSpecificRoutineListUseCase: GetSpecificRoutineListUseCase,  // 특정 취미의 활동 목록 조회
    private val getMyHobbyListUseCase: GetMyHobbyListUseCase,
    private val writeRoutineUseCase: WriteRoutineUseCase,
    private val getStickersUseCase: GetStickersUseCase,  // 스티커판 조회
    private val getAiRecommendedRoutinesUseCase: GetAiRecommendedRoutinesUseCase,  // ai 활동 추천
    private val getAiRecommendedRoutinesAgainUseCase: GetAiRecommendedRoutinesAgainUseCase,  // ai 활동 재조회
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val userLocalDataSource: UserLocalDataSource,
    private val createRoutinesUseCase: CreateRoutinesUseCase,  // 취미활동 생성
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<HomeSideEffect>() {

    private val _uiState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.toStateIn()
    fun fetchHomeHobbyData(hobbyId: Long? = null) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        flow {
            val domain = getHomeHobbyUseCase(hobbyId)
            val uiModel = domain.data?.toPresentation()
            emit(uiModel)
        }.catch { throwable ->
            throwable.printStackTrace()
            Timber.e("HomeHobbyData Fetch Error: $throwable")
            _uiState.update { it.copy(isLoading = false) } // 에러 시 로딩 종료
            val message = when (throwable) {
                is HttpException -> throwable.logAndExtractServerMessage(tag = "fetchHomeHobbyData")
                else -> null
            }
            snackbarManager.show(message ?: throwable.toUserMessage())
        }.collect { data ->
            // data(UiModel)가 null이 아닐 때만 업데이트 진행
            data?.let { uiModel ->
                Timber.e("Hobby First ID: ${uiModel.inProgressHobbies.getOrNull(0)?.hobbyId}")

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        // 리스트에서 첫 번째, 두 번째 취미 이름 추출
                        hobbyFirst = uiModel.inProgressHobbies.getOrNull(0)?.name ?: "",
//                        hobbyFirst = runCatching { uiModel.inProgressHobbies[0] }.getOrDefault(""),
                        hobbySecond = uiModel.inProgressHobbies.getOrNull(1)?.name ?: "",

                        // 전체 리스트 데이터 업데이트
                        inProgressHobbies = uiModel.inProgressHobbies,
                        routinePreview = uiModel.routinePreview,
                        aiCallRemaining = uiModel.aiCallRemaining,
                        aiCallRemainingCount = uiModel.aiCallRemainingCount,
                        greetingMessage = uiModel.greetingMessage,
                        userSummaryText = uiModel.userSummaryText,
                        recommendMessage = uiModel.recommendMessage,
                        // [참고] 아래 필드들은 현재 API 응답(data)에 없으므로 기존 상태를 유지하거나
                        // 다른 API를 통해 업데이트해야 합니다. 주석 처리하거나 제거하세요.
                        // stickerCnt = ...,
                        // isRecordedToday = ...,
                        // stickers = ...
                    )
                }
            } ?: _uiState.update { it.copy(isLoading = false) } // 데이터가 null인 경우 로딩만 해제
        }
    }

    fun fetchSpecificRoutineList(hobbyId: Long?, size: Int?) =
        viewModelScope.launch {  // 드롭 다운용 특정 취미 활동 목록 조회
//        _uiState.update { it.copy(isLoading = true) }

            flow {
                val response = getSpecificRoutineListUseCase(hobbyId, size)
                emit(response.data.routines)
            }.httpCatch(tag = "fetchSpecificRoutineList") { errorData ->

            }.collect { routines ->
                routines.map {
                    Timber.e("@###@#@#@ " + it.routineId + ", " + it.aiRecommended)
                }
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
                        routineList = routineUiModels
                    )
                }
            }
        }


    fun fetchStickerHistory(hobbyId: Long?, size: Int = 28, page: Int? = null) = viewModelScope.launch {
        flow {
            val response = getStickersUseCase(hobbyId, page, size)
            Timber.e("UseCase response: $response")
            emit(response)
        }.httpCatch(tag = "fetchStickerHistory") { errorData ->

        }.collect { result ->
            // ✅ Result 타입 처리 확인
            Timber.e("Collected result type: ${result::class.simpleName}")
            Timber.e("Result stickers: ${result.stickers}")

            val stickerInfo = result.toPresentation()
            val stickerList = result.stickers.map { it.toPresentation() }

            Timber.e("Mapped stickerInfo: $stickerInfo")
            Timber.e("Mapped stickerList size: ${stickerList.size}")

            _uiState.update { currentState ->
                Timber.e("@@@@@@@@@@result" + result)
                currentState.copy(
                    stickerInfo = stickerInfo,
                    stickers = stickerList
                )
            }

            Timber.e("State after update - stickers.size: ${_uiState.value.stickers.size}")
        }
    }

    fun selectRoutine(routineId: Int) {
        val selectedRoutine = _uiState.value.routineList.find { it.routineId == routineId }
        Timber.e("@#@#@#@# " + selectedRoutine)
        selectedRoutine?.let { routine ->
            _uiState.update { state ->
                state.copy(
                    routinePreview = RoutinePreviewUiModel(
                        routineId = routine.routineId,
                        content = routine.content,
                        isAiRecommended = routine.isAiRecommended
                    )
                )
            }
        }
    }

    /**
     * 다음 스티커 페이지로 이동
     */
    fun nextStickerPage() {
        val currentHobbyId = uiState.value.inProgressHobbies.find { it.isCurrent }?.hobbyId
        val currentApiPage = uiState.value.stickerInfo?.currentPage ?: 0  // ✅ API의 현재 페이지

        // ✅ API의 다음 페이지 요청
        val nextPage = currentApiPage + 1

        fetchStickerHistory(
            hobbyId = currentHobbyId,
            size = 28,
            page = nextPage  // ✅ 2, 3, 4...
        )
    }


    /**
     * 이전 스티커 페이지로 이동
     */
    fun previousStickerPage() {
        val currentHobbyId = uiState.value.inProgressHobbies.find { it.isCurrent }?.hobbyId
        val currentApiPage = uiState.value.stickerInfo?.currentPage ?: 1  // ✅ API의 현재 페이지

        // ✅ API의 이전 페이지 요청
        val prevPage = currentApiPage - 1

        fetchStickerHistory(
            hobbyId = currentHobbyId,
            size = 28,
            page = prevPage  // ✅ 1, 2, 3...
        )
    }

    fun getAiRecommendedRoutines(hobbyId: Long?) = viewModelScope.launch {
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
                    aiRoutineList = newAiRoutines,
                    aiRoutineLoaded = !it.aiRoutineLoaded,
                    isLoading = false,
                    aiCallCount = result.data.aiCallCount,
                    recommendedText = result.data.recommendedText,
                    errorData = null
                )
            }
        }
    }

    fun getAiRecommendedRoutinesAgain(hobbyId: Long?, type: String?= "ALL") = viewModelScope.launch {
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
                    aiRoutineList = aiRoutines,
                    aiRoutineLoaded = !it.aiRoutineLoaded,
                    isLoading = false,
                    recommendedText = result.data.message,
                    errorData = null
                )
            }
        }
    }

    fun createRoutines(hobbyId: Long?, routineList: List<Pair<Boolean, String>>, hobbyName: String? = null) =
        viewModelScope.launch {
            flow {
                emit(createRoutinesUseCase.invoke(hobbyId, routineList))
            }.httpCatch(tag = "createRoutines") { errorData ->
                when (errorData.errorClassName) {
                    "VALIDATION_ERROR" -> _sideEffectChannel.send(HomeSideEffect.CreateRoutinesError(errorData.message))
                }

            }.collect { result ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        routineId = result.data.createdRoutineNum
                    )
                }
                routineList.forEach { (isAi, activityName) ->
                    logEvent(AnalyticsEvents.activityAdded(
                        entryPoint = "ai_banner",
                        source = if (isAi) "ai_recommendation" else "manual",
                        hobbyName = hobbyName,
                        activityName = activityName
                    ))
                }
                _sideEffectChannel.send(HomeSideEffect.CreateRoutinesSuccess("AI 취미활동을 담았어요."))
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
                    state.copy(
                        nickName = data
                    )
                }
            }
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

}

