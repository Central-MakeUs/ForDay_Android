package com.forday.app.presentation.mypage.routinedetail

sealed interface RoutineDetailSideEffect {
    data object DeleteSuccess : RoutineDetailSideEffect
}
