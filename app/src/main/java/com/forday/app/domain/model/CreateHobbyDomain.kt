package com.forday.app.domain.model

data class CreateHobbyDomain(
    val status: Int,
    val isSuccess: Boolean,
    val data: CreateHobbyDataDomain
)

data class CreateHobbyDataDomain(
    val message: String,
    val hobbyId: Int
)