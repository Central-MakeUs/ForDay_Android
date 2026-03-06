package com.forday.app.presentation.modifyroutine

import androidx.lifecycle.viewModelScope
import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.domain.usecase.DeleteHobbyRoutineUseCase
import com.forday.app.domain.usecase.GetHobbyRoutineListUseCase
import com.forday.app.domain.usecase.ModifyHobbyRoutineUseCase
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
class ModifyRoutineViewModel @Inject constructor(
    private val getHobbyRoutineListUseCase: GetHobbyRoutineListUseCase,
    private val modifyHobbyRoutineUseCase: ModifyHobbyRoutineUseCase,
    private val deleteHobbyRoutineUseCase: DeleteHobbyRoutineUseCase,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<ModifyRoutineSideEffect>() {

    private val _uiState: MutableStateFlow<RoutinesUiState> = MutableStateFlow(RoutinesUiState())
    val uiState: StateFlow<RoutinesUiState> = _uiState.toStateIn()

    // ── MVI 단일 진입점 ──────────────────────────────────────────────
    fun onAction(action: ModifyRoutineAction) {
        when (action) {
            is ModifyRoutineAction.FetchHobbyRoutineList -> fetchHobbyRoutineList(action.hobbyId)
            is ModifyRoutineAction.ModifyRoutine -> modifyRoutine(action.routineId, action.content)
            is ModifyRoutineAction.DeleteRoutine -> deleteRoutine(action.routineId)
        }
    }

    // ── Private helpers ──────────────────────────────────────────────

    private fun fetchHobbyRoutineList(hobbyId: Long?) = viewModelScope.launch {
        if (hobbyId == null) {
            Timber.w("fetchHobbyRoutineList: hobbyId is null")
            _uiState.update {
                it.copy(
                    errorData = ErrorDataUiState(
                        message = "잘못된 접근입니다.",
                        errorType = ErrorDataUiState.ErrorType.TYPE_BACK,
                    )
                )
            }
            return@launch
        }

        flow {
            emit(getHobbyRoutineListUseCase(hobbyId))
        }.httpCatch(tag = "fetchHobbyRoutineList") { errorData ->
            _uiState.update { it.copy(errorData = errorData) }
        }.collect { data ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    routines = data.data.routines.toUiModelList(),
                    errorData = null,
                )
            }
        }
    }

    private fun modifyRoutine(routineId: Long, content: String) = viewModelScope.launch {
        flow {
            emit(modifyHobbyRoutineUseCase(routineId, content))
        }.httpCatch(tag = "modifyRoutine") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            _uiState.update { currentState ->
                currentState.copy(
                    routines = currentState.routines.map { routine ->
                        if (routine.routineId == routineId) {
                            routine.copy(content = content)
                        } else {
                            routine
                        }
                    }
                )
            }
            snackbarManager.show(data.data.message)
        }
    }

    private fun deleteRoutine(routineId: Long) = viewModelScope.launch {
        flow {
            emit(deleteHobbyRoutineUseCase(routineId))
        }.httpCatch(tag = "deleteRoutine") { errorData ->
            snackbarManager.show(errorData.message)
        }.collect { data ->
            _uiState.update { currentState ->
                currentState.copy(
                    routines = currentState.routines.filter { routine ->
                        routine.routineId != routineId
                    }
                )
            }
            snackbarManager.show(data.data.message)
        }
    }
}
