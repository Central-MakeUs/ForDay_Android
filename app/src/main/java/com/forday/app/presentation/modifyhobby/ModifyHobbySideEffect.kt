package com.forday.app.presentation.modifyhobby

sealed interface ModifyHobbySideEffect {
    data class DomainError(val error: String): ModifyHobbySideEffect
    data class Exception(val error: Throwable): ModifyHobbySideEffect
}