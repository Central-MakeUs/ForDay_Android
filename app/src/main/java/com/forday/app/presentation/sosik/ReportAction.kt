package com.forday.app.presentation.sosik

sealed interface ReportAction {
    data class ReportPosting(val recordId: Int, val reason: String) : ReportAction
    data class ReportUser(val userId: String, val reason: String) : ReportAction
    data class BlockUser(val userId: String) : ReportAction
}
