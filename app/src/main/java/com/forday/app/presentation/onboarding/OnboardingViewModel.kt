package com.forday.app.presentation.onboarding

import androidx.lifecycle.viewModelScope
import com.forday.app.core.logger.analytics.AnalyticsEvent
import com.forday.app.core.logger.analytics.AnalyticsManager
import com.forday.app.domain.usecase.CreateHobbyUseCase
import com.forday.app.domain.usecase.GetHobbyCardDataAgainUseCase
import com.forday.app.domain.usecase.GetHobbyDataUseCase
import com.forday.app.domain.usecase.GetOnboardingDataUseCase
import com.forday.app.domain.usecase.ModifyHobbyDurationUseCase
import com.forday.app.domain.usecase.ModifyHobbyExecutionCountUseCase
import com.forday.app.domain.usecase.ModifyHobbyTimeUseCase
import com.forday.app.domain.usecase.RecreateHobbyUseCase
import com.forday.app.domain.usecase.RemoveOnboardingDataUseCase
import com.forday.app.domain.usecase.SaveCreatedHobbyIdUseCase
import com.forday.app.domain.usecase.SaveOnboardingDataUseCase
import com.forday.app.presentation.BaseViewModel
import com.forday.app.presentation.common.SnackbarManager
import com.forday.app.presentation.httpCatch
import com.forday.app.presentation.onboarding.periodselect.JourneyMode
import com.forday.app.core.util.UserMessageCategory
import com.forday.app.core.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val getHobbyDataUseCase: GetHobbyDataUseCase,
    private val getHobbyCardDataAgainUseCase: GetHobbyCardDataAgainUseCase,
    private val getOnboardingDataUseCase: GetOnboardingDataUseCase,
    private val createHobbyUseCase: CreateHobbyUseCase,
    private val recreateHobbyUseCase: RecreateHobbyUseCase,
    private val saveOnboardingDataUseCase: SaveOnboardingDataUseCase,
    private val saveCreatedHobbyIdUseCase: SaveCreatedHobbyIdUseCase,
    private val removeOnboardingDataUseCase: RemoveOnboardingDataUseCase,
    private val modifyHobbyTimeUseCase: ModifyHobbyTimeUseCase,
    private val modifyHobbyExecutionCountUseCase: ModifyHobbyExecutionCountUseCase,
    private val modifyHobbyDurationUseCase: ModifyHobbyDurationUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<Unit>() {

    private val _uiState: MutableStateFlow<OnboardingUiState> =
        MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.toStateIn()

    init {
        restoreFromDataStore()
    }

    private fun restoreFromDataStore() = viewModelScope.launch {
        try {
            val data = getOnboardingDataUseCase().first()
            _uiState.update {
                it.copy(
                    hobbyId = data.hobbyId?.toInt(),
                    selectedHobbyId = data.hobbyInfoId?.toLong(),
                    selectedHobbyName = data.hobbyName,
                    selectedMinutes = data.hobbyTimeMinutes,
                    selectedPurpose = data.hobbyPurpose,
                    selectedFrequency = data.executionCount,
                    selectedJourneyMode = if (data.durationSet == true) JourneyMode.FORDAY_66 else null
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "restoreFromDataStore failed")
        }
    }

    fun logEvent(logEvent: String) {
        analyticsManager.logEvent(logEvent)
    }

    fun logEvent(event: AnalyticsEvent) {
        analyticsManager.logEvent(event)
    }

    fun fetchHobbyData() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        flow {
            emit(getHobbyDataUseCase())
        }.httpCatch("fetchHobbyData") { errorData ->
            _uiState.update { it.copy(errorData = errorData, isLoading = false) }
        }.collect { data ->
            _uiState.update {
                it.copy(
                    hobbies = data.hobbies.map { it.toPresentation() },
                    appVersion = data.appVersion,
                    isLoading = false
                )
            }
        }
    }

    fun showDialog() {
        _uiState.update { it.copy(showDialog = true) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(showDialog = false) }
    }

    fun saveHobbyInfo(hobbyId: Long?, hobbyName: String) {
        _uiState.update {
            it.copy(
                selectedHobbyId = hobbyId,
                customHobbyText = "",
                selectedHobbyName = hobbyName
            )
        }
        persistOnboardingData()
    }

    fun confirmCustomHobby(text: String) {
        _uiState.update {
            it.copy(
                customHobbyText = text,
                showDialog = false,
                selectedHobbyId = 0,
                selectedHobbyName = text
            )
        }
        persistOnboardingData()
    }

    fun savePurposes(purposes: Set<String>) {
        _uiState.update {
            it.copy(
                selectedPurpose = purposes.joinToString(","),
                customPurposeText = ""
            )
        }
        persistOnboardingData()
    }

    fun confirmCustomPurpose(text: String) {
        _uiState.update {
            it.copy(customPurposeText = text, selectedPurpose = "")
        }
        persistOnboardingData()
    }

    fun saveTime(minutes: Int) {
        _uiState.update { it.copy(selectedMinutes = minutes) }
        persistOnboardingData()
    }

    fun saveFrequency(frequency: Int) {
        _uiState.update { it.copy(selectedFrequency = frequency) }
        persistOnboardingData()
    }

    fun selectJourneyMode(mode: JourneyMode) {
        _uiState.update { it.copy(selectedJourneyMode = mode) }
        persistOnboardingData()
    }

    private fun persistOnboardingData() = viewModelScope.launch {
        val state = _uiState.value
        runCatching {
            saveOnboardingDataUseCase(
                state.selectedHobbyId,
                state.selectedHobbyName,
                state.selectedMinutes,
                state.selectedPurpose,
                state.selectedFrequency,
                state.selectedJourneyMode == JourneyMode.FORDAY_66
            )
        }.onFailure { Timber.e(it, "persistOnboardingData error") }
    }

    fun getOnboardingData() = viewModelScope.launch {
        getOnboardingDataUseCase()
            .catch { throwable ->
                snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
            }
            .collect { onboardingData ->
                _uiState.update {
                    it.copy(
                        hobbyId = onboardingData.hobbyId?.toInt(),
                        selectedHobbyId = onboardingData.hobbyInfoId?.toLong(),
                        selectedHobbyName = onboardingData.hobbyName,
                        selectedMinutes = onboardingData.hobbyTimeMinutes,
                        selectedPurpose = onboardingData.hobbyPurpose,
                        selectedFrequency = onboardingData.executionCount,
                        selectedJourneyMode = if (onboardingData.durationSet == true) JourneyMode.FORDAY_66 else JourneyMode.FREE
                    )
                }
            }
    }

    fun getHobbyCardDataAgain() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        flow {
            emit(getHobbyCardDataAgainUseCase().toPresentation())
        }.catch { throwable ->
            _uiState.update { it.copy(isLoading = false) }
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { data ->
            _uiState.update {
                it.copy(hobbies = data.hobbies, isLoading = false)
            }
        }
    }

    fun resetOnboardingState() = viewModelScope.launch {
        runCatching { removeOnboardingDataUseCase() }
            .onFailure { Timber.e("removeOnboardingData error: $it") }

        _uiState.update {
            it.copy(
                hobbyId = null,
                selectedHobbyId = null,
                selectedHobbyName = "",
                customHobbyText = "",
                selectedMinutes = null,
                selectedPurpose = "",
                customPurposeText = "",
                selectedFrequency = null,
                selectedJourneyMode = null,
                isOnboardingDataSaved = false,
                isHobbyRecreated = false
            )
        }
    }

    fun createHobby(
        selectedHobbyId: Long? = null,
        selectedHobbyName: String?,
        selectedMinutes: Int?,
        selectedPurpose: String?,
        selectedFrequency: Int?,
        selectedPeriod: JourneyMode?
    ) = viewModelScope.launch {
        flow {
            emit(
                createHobbyUseCase(
                    selectedHobbyId, selectedHobbyName, selectedMinutes,
                    selectedPurpose, selectedFrequency,
                    selectedPeriod == JourneyMode.FORDAY_66
                )
            )
        }.httpCatch("createHobby") { errorData ->
            if (errorData.errorClassName == "DUPLICATE_HOBBY_REQUEST") {
                handleDuplicateHobby(
                    selectedHobbyId, selectedHobbyName, selectedMinutes,
                    selectedPurpose, selectedFrequency, selectedPeriod
                )
            } else {
                snackbarManager.show(errorData.message)
            }
        }.collect { result ->
            _uiState.update {
                it.copy(
                    isOnboardingDataSaved = result.isSuccess,
                    hobbyId = result.data.hobbyId
                )
            }
            if (result.isSuccess == true) {
                saveCreatedHobbyIdUseCase(result.data.hobbyId.toLong())
                saveOnboardingDataUseCase(
                    selectedHobbyId, selectedHobbyName, selectedMinutes,
                    selectedPurpose, selectedFrequency,
                    selectedPeriod == JourneyMode.FORDAY_66
                )
            }
        }
    }

    private suspend fun handleDuplicateHobby(
        selectedHobbyId: Long?,
        selectedHobbyName: String?,
        selectedMinutes: Int?,
        selectedPurpose: String?,
        selectedFrequency: Int?,
        selectedPeriod: JourneyMode?
    ) {
        try {
            val onboardingData = getOnboardingDataUseCase().first()
            val hobbyId = onboardingData.hobbyId?.toLong()

            val result = recreateHobbyUseCase(
                hobbyId = hobbyId,
                hobbyInfoId = selectedHobbyId,
                hobbyName = selectedHobbyName,
                hobbyPurpose = selectedPurpose,
                hobbyTimeMinutes = selectedMinutes,
                executionCount = selectedFrequency,
                durationSet = selectedPeriod == JourneyMode.FORDAY_66
            )

            val isSuccess = result.status == 200 && result.isSuccess
            if (isSuccess) {
                saveCreatedHobbyIdUseCase(result.data.hobbyId)
                saveOnboardingDataUseCase(
                    selectedHobbyId, selectedHobbyName, selectedMinutes,
                    selectedPurpose, selectedFrequency,
                    selectedPeriod == JourneyMode.FORDAY_66
                )
                _uiState.update { it.copy(isOnboardingDataSaved = true, isHobbyRecreated = true) }
            } else {
                snackbarManager.show("취미 수정에 실패했어요. 잠시 후 다시 시도해주세요.")
                _uiState.update { it.copy(isHobbyRecreated = false) }
            }
        } catch (e: Exception) {
            Timber.e("handleDuplicateHobby error: $e")
            _uiState.update { it.copy(isHobbyRecreated = false) }
            snackbarManager.show(e.toUserMessage(UserMessageCategory.AUTH))
        }
    }

    fun recreateHobby(
        hobbyId: Long?, hobbyInfoId: Long?, hobbyName: String?,
        hobbyPurpose: String?, hobbyTimeMinutes: Int?,
        executionCount: Int?, durationSet: Boolean?
    ) = viewModelScope.launch {
        flow {
            emit(
                recreateHobbyUseCase(
                    hobbyId, hobbyInfoId, hobbyName, hobbyPurpose,
                    hobbyTimeMinutes, executionCount, durationSet
                )
            )
        }.catch { throwable ->
            _uiState.update { it.copy(isHobbyRecreated = false) }
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { result ->
            val isSuccess = result.status == 200 && result.isSuccess
            if (!isSuccess) {
                snackbarManager.show("취미 수정에 실패했어요. 잠시 후 다시 시도해주세요.")
            }
            _uiState.update { it.copy(isHobbyRecreated = isSuccess) }
        }
    }

    fun modifyHobbyTime(hobbyId: Long?, minutes: Int) = viewModelScope.launch {
        flow {
            emit(modifyHobbyTimeUseCase(hobbyId, minutes))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { data ->
            if (data.status != 200) {
                snackbarManager.show(data.data.message)
            }
        }
    }

    fun modifyHobbyExecutionCount(hobbyId: Long?, executionCount: Int) = viewModelScope.launch {
        flow {
            emit(modifyHobbyExecutionCountUseCase(hobbyId, executionCount))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { data ->
            if (data.status != 200) {
                snackbarManager.show(data.data.message)
            }
        }
    }

    fun modifyHobbyGoalDays(hobbyId: Long?, goalDays: Boolean) = viewModelScope.launch {
        flow {
            emit(modifyHobbyDurationUseCase(hobbyId, goalDays))
        }.catch { throwable ->
            snackbarManager.show(throwable.toUserMessage(UserMessageCategory.AUTH))
        }.collect { data ->
            if (data.status != 200) {
                snackbarManager.show(data.data.message)
            }
        }
    }
}
