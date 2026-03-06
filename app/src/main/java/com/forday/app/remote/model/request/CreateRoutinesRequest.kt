package com.forday.app.remote.model.request

import kotlinx.serialization.Serializable

/**
* 취미활동 추가 Request
* */

@Serializable
data class CreateRoutinesRequest(
    val activities: List<RoutineItem>
)

@Serializable
data class RoutineItem(
    val aiRecommended: Boolean,
    val content: String
)