package com.forday.app.domain.model

data class RoutineListDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: RoutineListDataDomain
)

data class RoutineListDataDomain(
    val routines: List<RoutineListItemDomain>
)

data class RoutineListItemDomain(
    val routineId: Int,
    val content: String,
    val aiRecommended: Boolean
)