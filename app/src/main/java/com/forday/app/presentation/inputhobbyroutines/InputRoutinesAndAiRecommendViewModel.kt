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

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: InputRoutinesAction) {
        when (action) {
            is InputRoutinesAction.SearchHobbyMatesRoutines -> searchHobbyMatesRoutines(action.selectedHobbyId)
            is InputRoutinesAction.InitHobbyName -> initHobbyName(action.hobbyName)
            is InputRoutinesAction.ResetInputState -> resetInputState()
            is InputRoutinesAction.GetAiRecommendedRoutines -> getAiRecommendedRoutines(action.hobbyId)
            is InputRoutinesAction.GetAiRecommendedRoutinesAgain -> getAiRecommendedRoutinesAgain(action.hobbyId, action.type)
            is InputRoutinesAction.GetUserNickname -> getUserNickname()
            is InputRoutinesAction.SetSelectedAiRoutine -> setSelectedAiRoutine(action.routine)
            is InputRoutinesAction.ClearSelectedAiRoutine -> clearSelectedAiRoutine()
            is InputRoutinesAction.SaveAiRoutines -> saveAiRoutines(action.routines)
            is InputRoutinesAction.CreateRoutines -> createRoutines(action.hobbyId, action.routineList)
        }
    }

    // ── Analytics ─────────────────────────────────────────────────────
    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun searchHobbyMatesRoutines(selectedHobbyId: Long?) = viewModelScope.launch {
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

    private fun initHobbyName(hobbyName: String?) {
        _uiState.update { it.copy(selectedHobbyName = hobbyName) }
    }

    private fun resetInputState() {
        _uiState.update { it.copy(selectedAiRoutine = null) }
    }

    private fun saveAiRoutines(routines: List<AiRoutineItemState>) = viewModelScope.launch {
        userLocalDataSource.saveAiRoutineList(routines)
    }

    private fun getOnboardingData() = viewModelScope.launch {
        getOnboardingDataUseCase()
            .httpCatch(tag = "getOnboardingData") { errorData ->
                snackbarManager.show(errorData.message)
            }
            .collect { onboardingData ->
                _uiState.update {
                    it.copy(selectedHobbyName = onboardingData.hobbyName)
                }
            }
    }

    private fun createRoutines(hobbyId: Long?, routineList: List<Pair<Boolean, String>>) =
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

    private fun getAiRecommendedRoutines(hobbyId: Long?) = viewModelScope.launch {
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

    private fun getAiRecommendedRoutinesAgain(hobbyId: Long?, type: String? = "LATEST") = viewModelScope.launch {
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

    private fun getUserNickname() = viewModelScope.launch {
        getUserNicknameUseCase()
            .httpCatch(tag = "getUserNickname") { errorData ->
                snackbarManager.show(errorData.message)
            }.collect { data ->
                _uiState.update { state ->
                    state.copy(nickname = data)
                }
            }
    }

    private fun setSelectedAiRoutine(routine: AiRoutineItemState) {
        _uiState.update { it.copy(selectedAiRoutine = routine) }
    }

    private fun clearSelectedAiRoutine() {
        _uiState.update { it.copy(selectedAiRoutine = null) }
    }
}
