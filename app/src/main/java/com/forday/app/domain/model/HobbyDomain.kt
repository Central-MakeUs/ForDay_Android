package com.forday.app.domain.model

/**
 * 취미 카드 도메인 모델
 */
data class HobbyCardDomain(
    val appVersion: String,
    val hobbies: List<HobbyDomain>
)

/**
 * 취미 도메인 모델
 */
data class HobbyDomain(
    val id: Long?,
    val name: String,
    val description: String,
    val imageCode: String
)