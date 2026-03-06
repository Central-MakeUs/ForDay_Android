package com.forday.app.presentation.modifyroutine

sealed interface ModifyRoutineAction {
    data class FetchHobbyRoutineList(val hobbyId: Long?) : ModifyRoutineAction
    data class ModifyRoutine(val routineId: Long, val content: String) : ModifyRoutineAction
    data class DeleteRoutine(val routineId: Long) : ModifyRoutineAction
}
