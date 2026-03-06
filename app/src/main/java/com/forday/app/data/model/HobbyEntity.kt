package com.forday.app.data.model

import com.forday.app.data.DataMapper
import com.forday.app.domain.model.HobbyCardDomain
import com.forday.app.domain.model.HobbyDomain

/**
 * 취미 카드 엔티티
 */

data class HobbyCardEntity(
    val appVersion: String,
    val hobbies: List<HobbyEntity>
) : DataMapper<HobbyCardDomain> {

    override fun toDomain(): HobbyCardDomain {
        return HobbyCardDomain(
            appVersion = appVersion,
            hobbies = hobbies.map { it.toDomain() }
        )
    }
}

/**
 * 취미 엔티티
 */
data class HobbyEntity(
    val id: Long?,
    val name: String,
    val description: String,
    val imageCode: String
) : DataMapper<HobbyDomain> {

    override fun toDomain(): HobbyDomain {
        return HobbyDomain(
            id = id,
            name = name,
            description = description,
            imageCode = imageCode
        )
    }
}