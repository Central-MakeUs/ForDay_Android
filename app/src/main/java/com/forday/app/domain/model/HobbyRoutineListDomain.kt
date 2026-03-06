package com.forday.app.domain.model

data class HobbyRoutineListDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: HobbyRoutineListDataDomain
)

data class HobbyRoutineListDataDomain(
    val routines: List<RoutineDataDomain>,
    val errorClassName: String?,
    val message: String
)

data class RoutineDataDomain(
    val routineId: Int,
    val content: String,
    val isAiRecommended: Boolean,
    val isDeletable: Boolean,
    val stickerCount: Int
)