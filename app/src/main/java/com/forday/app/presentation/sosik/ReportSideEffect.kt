package com.forday.app.presentation.sosik

sealed interface ReportSideEffect {
    data class ReportPostingSuccess(val reportedWriterId: String) : ReportSideEffect
    data object ReportUserSuccess : ReportSideEffect
    data object BlockUserSuccess : ReportSideEffect
}
