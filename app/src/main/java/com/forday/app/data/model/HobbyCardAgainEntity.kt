package com.forday.app.data.model

import com.forday.app.domain.model.HobbyCardAgainDomain
import com.forday.app.domain.model.HobbyItemDomain

data class HobbyCardAgainEntity(
    val hobbies: List<HobbyItemEntity>
) {
    fun toDomain(): HobbyCardAgainDomain {
        return HobbyCardAgainDomain(
            hobbies = hobbies.map { it.toDomain() }
        )
    }
}

data class HobbyItemEntity(
    val id: Long,
    val name: String,
    val description: String,
    val imageCode: String
) {
    fun toDomain(): HobbyItemDomain {
        return HobbyItemDomain(
            id = id,
            name = name,
            description = description,
            imageCode = imageCode
        )
    }
}