package com.forday.app.domain.model

data class SearchHobbyMateRoutinesDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: SearchHobbyMateRoutinesDataDomain
)

data class SearchHobbyMateRoutinesDataDomain(
    val message: String,
    val activities: List<RoutineItemDomain>
)

data class RoutineItemDomain(
    val routineId: Int,
    val content: String
)
