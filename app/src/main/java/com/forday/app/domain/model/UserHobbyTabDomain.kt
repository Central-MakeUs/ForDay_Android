package com.forday.app.domain.model

data class UserHobbyTabDomain(
    val inProgressCount: Int?,
    val hobbyCardCount: Int?,
    val hobbies: List<HobbyTabDomain>?
)

data class HobbyTabDomain(
    val id: Int?,
    val name: String?,
    val imageUrl: String?,
    val status: String?
)