package com.forday.app.domain.model

data class HobbyCardAgainDomain(
    val hobbies: List<HobbyItemDomain>
)

data class HobbyItemDomain(
    val id: Long,
    val name: String,
    val description: String,
    val imageCode: String
)