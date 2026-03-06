package com.forday.app.data.model

import com.forday.app.domain.model.HobbyTabDomain
import com.forday.app.domain.model.UserHobbyTabDomain

data class UserHobbyTabEntity(
    val inProgressCount: Int,
    val hobbyCardCount: Int,
    val hobbies: List<HobbyTabEntity>
)

data class HobbyTabEntity(
    val hobbyId: Int,
    val name: String,
    val imageUrl: String?,
    val status: String
)

/**
 * Data Mapper: Data -> Domain
 */
fun UserHobbyTabEntity.toDomain() = UserHobbyTabDomain(
    inProgressCount = inProgressCount,
    hobbyCardCount = hobbyCardCount,
    hobbies = hobbies.map { it.toDomain() }
)

fun HobbyTabEntity.toDomain() = HobbyTabDomain(
    id = hobbyId,
    name = name,
    imageUrl = imageUrl,
    status = status
)