package com.forday.app.domain.model

data class HobbyMainImageDomain(
    val hobbyId: Int,
    val recordId: Long,
    val imageUrl: String,
    val message: String
)