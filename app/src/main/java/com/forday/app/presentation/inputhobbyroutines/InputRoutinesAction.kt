package com.forday.app.presentation.inputhobbyroutines

sealed interface InputRoutinesAction {
    // 데이터 로드
    data class SearchHobbyMatesRoutines(val selectedHobbyId: Long?) : InputRoutinesAction
    data class InitHobbyName(val hobbyName: String?) : InputRoutinesAction
    data object ResetInputState : InputRoutinesAction
    data class GetAiRecommendedRoutines(val hobbyId: Long?) : InputRoutinesAction
    data class GetAiRecommendedRoutinesAgain(val hobbyId: Long?, val type: String? = "LATEST") : InputRoutinesAction
    data object GetUserNickname : InputRoutinesAction

    // AI 루틴 선택
    data class SetSelectedAiRoutine(val routine: AiRoutineItemState) : InputRoutinesAction
    data object ClearSelectedAiRoutine : InputRoutinesAction
    data class SaveAiRoutines(val routines: List<AiRoutineItemState>) : InputRoutinesAction

    // 루틴 생성
    data class CreateRoutines(val hobbyId: Long?, val routineList: List<Pair<Boolean, String>>) : InputRoutinesAction
}
