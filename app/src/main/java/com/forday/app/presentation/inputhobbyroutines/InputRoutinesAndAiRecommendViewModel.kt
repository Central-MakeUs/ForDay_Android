package com.forday.app.presentation.inputhobbyroutines

import androidx.lifecycle.viewModelScope
import com.forday.app.core.datastore.UserLocalDataSource
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsEvents
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.domain.usecase.CreateRoutinesUseCase
import com.forday.app.domain.usecase.GetAiRecommendedRoutinesAgainUseCase
import com.forday.app.domain.usecase.GetAiRecommendedRoutinesUseCase
import com.forday.app.domain.usecase.GetHobbyMateRoutinesUseCase
import com.forday.app.domain.usecase.GetOnboardingDataUseCase
import com.forday.app.domain.usecase.GetUserNicknameUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InputRoutinesAndAiRecommendViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getHobbyMateRoutines: GetHobbyMateRoutinesUseCase,
    private val createRoutinesUseCase: CreateRoutinesUseCase,
    private val getAiRecommendedRoutinesUseCase: GetAiRecommendedRoutinesUseCase,
    private val getAiRecommendedRoutinesAgainUseCase: GetAiRecommendedRoutinesAgainUseCase,
    private val getOnboardingDataUseCase: GetOnboardingDataUseCase,
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val userLocalDataSource: UserLocalDataSource,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<InputRoutinesAndAiRecommendSideEffect>() {

    private val _uiState: MutableStateFlow<RoutinesState> = MutableStateFlow(RoutinesState())
    val uiState: StateFlow<RoutinesState> = _uiState.toStateIn()

    init {
        getOnboardingData()
    }

    fun searchHobbyMatesRoutines(selectedHobbyId: Long?) = viewModelScope.launch {  // 나와 취미가 비슷한 사람들의 루틴 추천
            flow {
                emit(getHobbyMateRoutines(selectedHobbyId))
            }.httpCatch(tag = "searchHobbyMatesRoutines") { errorData ->
                snackbarManager.show(errorData.message)
            }.collect { result ->
                _uiState.update {
                    it.copy(
                        hobbymateRoutines = result.data.activities.map { it.content },
                        isLoading = false
                    )
                }
            }
        }

    fun initHobbyName(hobbyName: String?) {
        _uiState.update { it.copy(selectedHobbyName = hobbyName) }
    }

    fun resetInputState() {
        _uiState.update { it.copy(selectedAiRoutine = null) }
    }

    fun saveAiRoutines(routines: List<AiRoutineItemState>) = viewModelScope.launch {
        userLocalDataSource.saveAiRoutineList(routines)
    }

    private fun getOnboardingData() = viewModelScope.launch {
        getOnboardingDataUseCase()
            .httpCatch(tag = "getOnboardingData") { errorData ->
                snackbarManager.show(errorData.message)
            }
            .collect { onboardingData ->
                _uiState.update {
                    it.copy(
                        selectedHobbyName = onboardingData.hobbyName,
                    )
                }
            }
    }

    fun createRoutines(hobbyId: Long?, routineList: List<Pair<Boolean, String>>) =  // 취미 활동 생성
        viewModelScope.launch {
            flow {
                emit(createRoutinesUseCase.invoke(hobbyId, routineList))
            }.httpCatch(tag = "createRoutines") { errorData ->
                snackbarManager.show(errorData.message)
            }.collect { result ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        routineId = result.data.createdRoutineNum
                    )
                }
                routineList.forEach { (isAi, activityName) ->
                    logEvent(AnalyticsEvents.activityAdded(
                        entryPoint = "activity_list_plus",
                        source = if (isAi) "ai_recommendation" else "manual",
                        hobbyName = _uiState.value.selectedHobbyName,
                        activityName = activityName
                    ))
                }
                _sideEffectChannel.send(InputRoutinesAndAiRecommendSideEffect.CreateRoutinesSuccess)
            }
        }

    fun getAiRecommendedRoutines(hobbyId: Long?) = viewModelScope.launch {  // AI 추천 활동 조회
        _uiState.update { it.copy(isLoading = true) }

        flow {
            emit(getAiRecommendedRoutinesUseCase(hobbyId))
        }.httpCatch(tag = "getAiRecommendedRoutines") { errorData ->
            when (errorData.errorClassName) {
                "AI_CALL_LIMIT_EXCEEDED" -> getAiRecommendedRoutinesAgain(hobbyId)
                else -> _uiState.update { it.copy(isLoading = false, errorData = errorData) }
            }
        }.collect { data ->
            _uiState.update {
                it.copy(
                    aiRoutineList = data.data.routines.map { it.toPresentation() },
                    aiCallCount = data.data.aiCallCount,
                    recommendedText = data.data.recommendedText,
                    isLoading = false,
                    errorData = null
                )
            }
        }
    }

    fun getAiRecommendedRoutinesAgain(hobbyId: Long?, type: String? = "LATEST") = viewModelScope.launch {  // AI호출횟수 다 썼을 때 마지막 데이터 불러오기
        _uiState.update { it.copy(isLoading = true) }

        flow {
            emit(getAiRecommendedRoutinesAgainUseCase(hobbyId, type))
        }.httpCatch(tag = "getAiRecommendedRoutinesAgain") { errorData ->
            _uiState.update { it.copy(isLoading = false, errorData = errorData) }
        }.collect { data ->
            _uiState.update {
                it.copy(
                    aiRoutineList = data.data.activityItems.map { item ->
                        AiRoutineItemState(
                            routineId = item.itemId,
                            topic = "",
                            content = item.content,
                            description = item.description
                        )
                    },
                    recommendedText = data.data.message,
                    isLoading = false,
                    errorData = null
                )
            }
        }
    }

    fun getUserNickname() = viewModelScope.launch {
        getUserNicknameUseCase()
            .httpCatch(tag = "getUserNickname") { errorData ->
                snackbarManager.show(errorData.message)
            }.collect { data ->
                _uiState.update { state ->
                    state.copy(
                        nickname = data
                    )
                }
            }
    }

    fun setSelectedAiRoutine(routine: AiRoutineItemState) {
        _uiState.update { it.copy(selectedAiRoutine = routine) }
    }

    fun clearSelectedAiRoutine() {
        _uiState.update { it.copy(selectedAiRoutine = null) }
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

}
