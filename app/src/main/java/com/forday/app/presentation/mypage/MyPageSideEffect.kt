package com.forday.app.presentation.mypage

sealed interface MyPageSideEffect {

    data class DomainError(val error: String): MyPageSideEffect
    data class Exception(val error: Throwable): MyPageSideEffect

}