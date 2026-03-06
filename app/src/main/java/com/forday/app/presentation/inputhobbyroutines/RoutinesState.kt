package com.forday.app.presentation.inputhobbyroutines


import com.forday.app.core.designsystem.component.state.ErrorDataUiState
import com.forday.app.domain.model.AiRoutineItemDomain
import com.forday.app.presentation.inputhobbyroutines.screen.RoutineItem
import kotlinx.serialization.Serializable


data class RoutinesState(
    val hobbymateRoutines: List<String> = emptyList(),
    val directInputRoutines: List<String> = emptyList(),
    val aiRoutineList: List<AiRoutineItemState> = emptyList(),
    val aiCallCount: Int = 0,
    val recommendedText: String = "",
    val hobbyId: Long? = null,
    val selectedHobbyName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val routineId: Int? = null,
    val nickname: String? = null,
    val selectedAiRoutine: AiRoutineItemState? = null,
    val errorData: ErrorDataUiState? = null,
)

@Serializable   // TODO 나중에 userLocalDataSource에 저장할 데이터클래스 별도 생성해야 함 (임시)
data class AiRoutineItemState(
    val routineId: Int,
    val topic: String,
    val content: String,
    val description: String
)

fun AiRoutineItemDomain.toPresentation(): AiRoutineItemState {
    return AiRoutineItemState(
        routineId = this.routineId,
        topic = this.topic,
        content = this.content,
        description = this.description
    )
}