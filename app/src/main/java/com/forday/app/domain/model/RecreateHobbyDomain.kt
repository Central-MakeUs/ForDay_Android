package com.forday.app.domain.model

data class RecreateHobbyDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: RecreateHobbyDataDomain
)

data class RecreateHobbyDataDomain(
    val hobbyId: Long,
    val hobbyInfoId: Long,
    val hobbyName: String,
    val hobbyPurpose: String,
    val hobbyTimeMinutes: Int,
    val executionCount: Int,
    val goalDays: Int
)
