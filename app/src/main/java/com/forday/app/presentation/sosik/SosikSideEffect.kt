package com.forday.app.presentation.sosik

sealed interface SosikSideEffect {
    data class DomainError(val message: String) : SosikSideEffect
    data class Exception(val throwable: Throwable) : SosikSideEffect
}
