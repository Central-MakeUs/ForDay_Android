package com.forday.app.presentation.record

sealed interface RecordRoutineSideEffect {
    data class WriteSuccess(val recordId: Long) : RecordRoutineSideEffect
    data class ModifySuccess(val recordId: Long) : RecordRoutineSideEffect
}
